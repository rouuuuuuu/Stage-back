import logging
import pandas as pd
from typing import Dict, Any, List
import unicodedata
import os
import fitz  # PyMuPDF
import pytesseract
from PIL import Image, ImageEnhance
import io
import camelot
import numpy as np
import re

# Configuration OCR
pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

# Configuration des logs
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("pdf_scanned_processor.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

def process_pdf_scanned_file(file_path: str) -> Dict[str, Any]:
    """
    Lit un fichier PDF numérisé, extrait le texte et les tableaux via OCR.
    
    Args:
        file_path (str): Chemin vers le fichier PDF (.pdf).
    
    Returns:
        Dict[str, Any]: Dictionnaire contenant le type de fichier, le texte extrait,
                       et la liste des tableaux sous forme de DataFrames.
    
    Raises:
        FileNotFoundError: Si le fichier n'existe pas.
        ValueError: Si le format du fichier n'est pas supporté.
        Exception: Pour les erreurs générales lors du traitement.
    """
    try:
        # Vérifier l'existence et le format du fichier
        if not os.path.exists(file_path):
            logger.error(f"Fichier introuvable: {file_path}")
            raise FileNotFoundError(f"Fichier introuvable: {file_path}")
        
        ext = os.path.splitext(file_path)[1].lower()
        if ext != '.pdf':
            logger.error(f"Format non supporté: {ext}")
            raise ValueError(f"Format non supporté: {ext}")
        
        logger.info(f"Traitement du fichier PDF numérisé: {file_path}")
        
        # Initialisation des résultats
        text_parts = []
        tables = []
        
        # Lire le fichier PDF
        with fitz.open(file_path) as doc:
            for page_num, page in enumerate(doc, 1):
                logger.info(f"Traitement de la page {page_num}")
                try:
                    # Extraire le texte via OCR
                    pix = page.get_pixmap(matrix=fitz.Matrix(300/72, 300/72))  # Résolution 300 DPI
                    img = Image.open(io.BytesIO(pix.tobytes())).convert('L')  # Convertir en niveaux de gris
                    img = ImageEnhance.Contrast(img).enhance(2.0)  # Augmenter le contraste
                    img_path = f"temp_page_{page_num}.png"
                    img.save(img_path, format='PNG')
                    logger.info(f"Image temporaire générée: {img_path}")
                    
                    # Créer un PDF temporaire pour camelot
                    temp_pdf = f"temp_page_{page_num}.pdf"
                    pdf_doc = fitz.open()
                    pdf_page = pdf_doc.new_page(width=pix.width, height=pix.height)
                    pdf_page.insert_image(pdf_page.rect, pixmap=pix)
                    pdf_doc.save(temp_pdf)
                    pdf_doc.close()
                    logger.info(f"PDF temporaire généré: {temp_pdf}")
                    
                    # Extraire le texte via OCR
                    text = pytesseract.image_to_string(img, lang='fra+eng+ara', config='--psm 6')
                    if text.strip():
                        text_parts.append(text.strip())
                        logger.info(f"Texte extrait de la page {page_num}: {len(text)} caractères")
                    else:
                        logger.info(f"Aucun texte extrait de la page {page_num}")
                    
                    # Extraire les tableaux avec camelot (lattice + stream)
                    try:
                        # Essayer d'abord avec lattice (tableaux avec lignes)
                        camelot_tables = camelot.read_pdf(
                            temp_pdf,
                            flavor='lattice',
                            suppress_stdout=True,
                            line_scale=50
                        )
                        logger.info(f"Camelot (lattice) a trouvé {len(camelot_tables)} tableaux")

                        # Si aucun tableau valide trouvé, essayer avec stream (sans lignes)
                        if len(camelot_tables) == 0 or all(table.parsing_report.get('accuracy', 0) <= 80 for table in camelot_tables):
                            logger.info("Aucun tableau valide avec lattice, tentative avec stream...")
                            camelot_tables = camelot.read_pdf(
                                temp_pdf,
                                flavor='stream',
                                suppress_stdout=True,
                                edge_tol=500,
                                row_tol=10,
                                column_tol=10
                            )
                            logger.info(f"Camelot (stream) a trouvé {len(camelot_tables)} tableaux")
                        
                        for table_num, table in enumerate(camelot_tables, 1):
                            accuracy = table.parsing_report.get('accuracy', 'N/A')
                            logger.info(f"Tableau {table_num} page {page_num}, accuracy: {accuracy}")
                            df = _clean_table(table.df)
                            if _validate_table(df):
                                tables.append(df)
                                logger.info(f"Tableau {table_num} extrait de la page {page_num}: {len(df)} lignes, {len(df.columns)} colonnes")
                            else:
                                logger.info(f"Tableau camelot {table_num} page {page_num} rejeté: validation échouée")
                    
                    except Exception as e:
                        logger.warning(f"Échec camelot pour page {page_num}: {e}, tentative avec pytesseract")
                        # Fallback OCR pour tableaux
                        table_data = pytesseract.image_to_data(img, lang='fra+eng+ara', config='--psm 6', output_type=pytesseract.Output.DATAFRAME)
                        if not table_data.empty:
                            df = _process_tesseract_table(table_data)
                            if _validate_table(df):
                                tables.append(df)
                                logger.info(f"Tableau OCR extrait de la page {page_num}: {len(df)} lignes, {len(df.columns)} colonnes")
                            else:
                                logger.info(f"Tableau pytesseract page {page_num} rejeté: validation échouée")
                    
                    # Détection heuristique de tableaux dans le texte
                    if text.strip():
                        heuristic_tables = _detect_tables_in_text(text)
                        for i, df in enumerate(heuristic_tables):
                            if _validate_table(df):
                                tables.append(df)
                                logger.info(f"Tableau heuristique {i+1} extrait de la page {page_num}: {len(df)} lignes, {len(df.columns)} colonnes")
                    
                    # Nettoyer les fichiers temporaires
                    for file in [img_path, temp_pdf]:
                        if os.path.exists(file):
                            os.remove(file)
                            logger.info(f"Fichier temporaire supprimé: {file}")
                
                except Exception as e:
                    logger.warning(f"Erreur lors du traitement de la page {page_num}: {e}")
                    continue
        
        # Combiner le texte de toutes les pages
        full_text = _clean_text('\n'.join(text_parts)) if text_parts else ""
        
        logger.info(f"Traitement terminé pour {file_path}: {len(tables)} tableaux extraits, {len(full_text)} caractères")
        
        return {
            'type': 'pdf_scanned',
            'text': full_text,
            'tables': tables
        }
    
    except Exception as e:
        logger.error(f"Erreur lors du traitement du fichier PDF numérisé: {e}")
        raise

def _clean_table(df: pd.DataFrame) -> pd.DataFrame:
    """
    Nettoie un DataFrame en supprimant les lignes/colonnes vides et en normalisant les cellules.
    
    Args:
        df (pd.DataFrame): DataFrame à nettoyer.
    
    Returns:
        pd.DataFrame: DataFrame nettoyé.
    """
    if df.empty:
        logger.info("DataFrame vide, retour direct")
        return df
    
    # Supprimer les lignes et colonnes entièrement vides
    df = df.dropna(how='all').dropna(axis=1, how='all')
    
    # Remplir les valeurs NaN par des chaînes vides
    df = df.fillna('')
    
    # Supprimer les lignes où toutes les cellules sont vides après strip
    df = df[df.apply(lambda x: x.astype(str).str.strip().str.len().sum() > 0, axis=1)]
    
    # Nettoyer chaque cellule
    for col in df.columns:
        df[col] = df[col].apply(_clean_cell)
    
    # Supprimer la première colonne si elle est entièrement vide
    if len(df.columns) > 1 and df.iloc[:, 0].astype(str).str.strip().eq('').all():
        df = df.drop(df.columns[0], axis=1)
    
    return df

def _clean_cell(cell: Any) -> str:
    """
    Nettoie une cellule en normalisant le texte et en supprimant les caractères indésirables.
    
    Args:
        cell: Contenu de la cellule (peut être str, int, float, etc.).
    
    Returns:
        str: Texte nettoyé de la cellule.
    """
    if cell is None:
        return ""
    if isinstance(cell, (int, float)):
        return str(cell)
    cell = unicodedata.normalize('NFKD', str(cell))
    return ' '.join(cell.split()).strip()

def _validate_table(df: pd.DataFrame) -> bool:
    """
    Valide un DataFrame pour s'assurer qu'il représente un tableau significatif.
    
    Args:
        df (pd.DataFrame): DataFrame à valider.
    
    Returns:
        bool: True si le tableau est valide, False sinon.
    """
    if df.empty or len(df) < 2 or len(df.columns) < 2:
        logger.info("Tableau rejeté: trop petit ou vide")
        return False
    
    # Vérifier que ce n'est pas une seule cellule avec beaucoup de texte
    if len(df) == 1 and len(df.columns) == 1:
        cell_text = str(df.iloc[0, 0])
        if len(cell_text) > 100:
            logger.info("Tableau rejeté: une seule cellule avec beaucoup de texte")
            return False
    
    header = df.iloc[0]
    if header.astype(str).str.strip().eq('').all() or header.astype(str).str.isnumeric().all():
        logger.info("Tableau rejeté: en-tête vide ou numérique")
        return False
    
    # Vérifier la densité des cellules non vides
    non_empty_cells = df.apply(lambda x: x.astype(str).str.strip().ne('').sum(), axis=1)
    if non_empty_cells.mean() < 0.3 * len(df.columns):
        logger.info("Tableau rejeté: trop de cellules vides")
        return False
    
    return True

def _clean_text(text: str) -> str:
    """
    Nettoie le texte en supprimant les lignes vides, les doublons, et les caractères indésirables.
    
    Args:
        text (str): Texte brut extrait.
    
    Returns:
        str: Texte nettoyé.
    """
    if not text:
        return ""
    
    text = unicodedata.normalize('NFKD', text)
    lines = []
    seen = set()
    for line in text.splitlines():
        line = line.strip()
        if not line:
            continue
        normalized_line = ''.join(c for c in unicodedata.normalize('NFKD', line.lower()) if c.isalnum() or c.isspace())
        if normalized_line in seen:
            continue
        line = ''.join(c for c in line if c.isalnum() or c.isspace() or c in '.,:;!?()€$+-')
        lines.append(line)
        seen.add(normalized_line)
    
    return '\n'.join(lines)

def _process_tesseract_table(data: pd.DataFrame) -> pd.DataFrame:
    """
    Traite les données OCR de pytesseract pour construire un tableau.
    Version améliorée avec clustering spatial.
    """
    data = data[data['text'].notnull() & (data['text'].str.strip() != '')].copy()
    if data.empty:
        logger.info("Aucun tableau détecté via pytesseract: données vides")
        return pd.DataFrame()
    
    # Trier par position verticale
    data = data.sort_values(['top', 'left']).reset_index(drop=True)
    
    # Regrouper les lignes par proximité verticale
    lines = []
    current_line = []
    current_top = data.iloc[0]['top']
    
    for _, row in data.iterrows():
        if abs(row['top'] - current_top) > 15:  # Nouvelle ligne si écart > 15px
            if current_line:
                lines.append(current_line)
            current_line = []
            current_top = row['top']
        current_line.append(row)
    
    if current_line:
        lines.append(current_line)
    
    # Pour chaque ligne, trier les mots par position horizontale
    table = []
    for line in lines:
        sorted_line = sorted(line, key=lambda x: x['left'])
        row_text = []
        current_word = ""
        current_left = 0
        
        for word in sorted_line:
            if current_word == "":
                current_word = word['text']
                current_left = word['left']
            else:
                # Si le mot est proche du précédent, concaténer
                if word['left'] - (current_left + len(current_word) * 10) < 50:
                    current_word += " " + word['text']
                else:
                    row_text.append(current_word)
                    current_word = word['text']
                    current_left = word['left']
        
        if current_word:
            row_text.append(current_word)
        
        if len(row_text) >= 2:  # Seulement si au moins 2 colonnes
            table.append(row_text)
    
    if not table or len(table) < 2:
        logger.info("Aucun tableau détecté via pytesseract: moins de 2 lignes ou colonnes")
        return pd.DataFrame()
    
    # Créer un DataFrame avec un nombre de colonnes fixe
    max_cols = max(len(row) for row in table)
    df = pd.DataFrame([row + [''] * (max_cols - len(row)) for row in table])
    return df

def _parse_product_line(line: str) -> List[str]:
    """
    Parse une ligne de produit et retourne une liste de colonnes.
    
    Args:
        line (str): Ligne de produit à parser.
    
    Returns:
        List[str]: Liste des colonnes extraites.
    """
    line = line.strip()
    if not line:
        return []
    
    # Nettoyer les caractères indésirables
    line = re.sub(r'\s+', ' ', line)  # Normaliser les espaces
    
    # Pattern pour les lignes de produits : CODE DESCRIPTION QUANTITÉ PRIX_UNITAIRE TAXE TOTAL
    # Exemple: "9905-389 CONTROL - PROACT IV DR(0-200MA) 5 839,76 0,00 5 839,76"
    
    # Méthode 1: Parsing intelligent basé sur les positions
    parts = line.split()
    
    if len(parts) >= 6:
        # Identifier le code produit (format XXXX-XXX)
        code_index = -1
        for i, part in enumerate(parts):
            if re.match(r'^\d{4,}-\d{3,}', part):
                code_index = i
                break
        
        if code_index >= 0:
            code = parts[code_index]
            
            # Trouver les montants (nombres avec virgules)
            amounts = []
            amount_indices = []
            for i, part in enumerate(parts):
                if re.match(r'^[\d\s,]+$', part.replace(' ', '')) and ',' in part:
                    amounts.append(part.replace(' ', ''))
                    amount_indices.append(i)
            
            # S'il y a au moins 3 montants, c'est probablement Qté, PU, TVA, Total
            if len(amounts) >= 3:
                # Le dernier montant est le total
                total = amounts[-1]
                # L'avant-dernier est la TVA
                tax = amounts[-2] if len(amounts) >= 2 else ""
                # L'avant-avant-dernier est le PU
                unit_price = amounts[-3] if len(amounts) >= 3 else ""
                
                # La quantité est le montant juste avant le PU
                quantity = ""
                if len(amount_indices) >= 3:
                    qty_index = amount_indices[-3]
                    quantity = parts[qty_index].replace(' ', '')
                
                # La description est entre le code et les montants
                desc_start = code_index + 1
                desc_end = amount_indices[0] if amount_indices else len(parts)
                description_parts = parts[desc_start:desc_end]
                description = ' '.join(description_parts).strip()
                
                return [code, description, quantity, unit_price, tax, total]
    
    # Méthode 2: Parsing basé sur des motifs spécifiques
    # Chercher des patterns plus précis
    pattern1 = r'(\d{4,}-\d{3,})\s+(.+?)\s+(\d+(?:\s*\d+)*)\s+([\d\s,]+)\s+([\d\s,]+)\s+([\d\s,]+)'
    match1 = re.search(pattern1, line)
    
    if match1:
        return [
            match1.group(1),  # Code
            match1.group(2).strip(),  # Description
            match1.group(3).replace(' ', ''),  # Quantité
            match1.group(4).replace(' ', ''),  # PU HT
            match1.group(5).replace(' ', ''),  # TVA
            match1.group(6).replace(' ', '')   # Total HT
        ]
    
    # Méthode 3: Parsing basique
    if len(parts) >= 6:
        # Essayer de reconstruire de manière plus simple
        code = ""
        description = ""
        quantity = ""
        unit_price = ""
        tax = ""
        total = ""
        
        # Trouver le code
        for part in parts:
            if re.match(r'^\d{4,}-\d{3,}', part):
                code = part
                break
        
        # Trouver les montants
        amounts = [part for part in parts if re.match(r'^[\d\s,]+$', part.replace(' ', '')) and ',' in part]
        
        if len(amounts) >= 3:
            total = amounts[-1].replace(' ', '')
            tax = amounts[-2].replace(' ', '') if len(amounts) >= 2 else ""
            unit_price = amounts[-3].replace(' ', '') if len(amounts) >= 3 else ""
        
        # Reconstruire la description
        if code:
            try:
                code_idx = parts.index(code)
                # Trouver le premier montant
                amount_idx = len(parts)
                for i, part in enumerate(parts):
                    if re.match(r'^[\d\s,]+$', part.replace(' ', '')) and ',' in part:
                        amount_idx = i
                        break
                
                desc_parts = parts[code_idx + 1:amount_idx]
                description = ' '.join(desc_parts).strip()
            except:
                pass
        
        if code and unit_price and total:
            return [code, description, "", unit_price, tax, total]
    
    return []

def _detect_tables_in_text(text: str) -> List[pd.DataFrame]:
    """
    Détecte les tableaux potentiels dans le texte extrait via heuristique.
    
    Args:
        text (str): Texte extrait du PDF.
    
    Returns:
        List[pd.DataFrame]: Liste de DataFrames représentant les tableaux détectés.
    """
    tables = []
    
    # Diviser le texte en lignes
    lines = text.split('\n')
    
    # Chercher des motifs de tableaux de produits
    product_lines = []
    header_line = ""
    collecting_products = False
    
    for i, line in enumerate(lines):
        line = line.strip()
        if not line:
            continue
            
        # Chercher l'en-tête de tableau (motifs courants)
        if re.search(r'(Nom.*Code.*Description)|(Code.*Description.*Qté)|(Description.*PU.*Total)', line, re.IGNORECASE):
            header_line = line
            collecting_products = True
            continue
            
        # Si on est en train de collecter des produits
        if collecting_products:
            # Arrêter si on rencontre une ligne qui n'est clairement pas un produit
            if re.search(r'(Total|Sous-total|TVA|Montant|Signature|Page)', line, re.IGNORECASE) and \
               not re.search(r'\d+[,\.]\d+', line):
                collecting_products = False
                continue
                
            # Chercher les lignes de produits (code produit + prix)
            if re.match(r'^\d{4,}-\d{3,}', line) or \
               (re.search(r'\d+[,\.]\d+', line) and len(line.split()) >= 4):
                product_lines.append(line)
    
    # Si on a trouvé des lignes de produits, créer un tableau
    if len(product_lines) >= 2:
        logger.info(f"Tableau heuristique détecté: {len(product_lines)} lignes de produits")
        
        # Parser les lignes de produits
        parsed_lines = []
        for line in product_lines:
            parsed_line = _parse_product_line(line)
            if parsed_line and len(parsed_line) >= 4:  # Au moins code, description, PU, total
                parsed_lines.append(parsed_line)
        
        if parsed_lines:
            # Créer un DataFrame avec des colonnes nommées
            max_cols = max(len(row) for row in parsed_lines) if parsed_lines else 0
            if max_cols >= 6:
                column_names = ['Code', 'Description', 'Qté', 'PU HT', 'TVA', 'Total HT']
                # Compléter les lignes avec des colonnes vides si nécessaire
                completed_lines = []
                for row in parsed_lines:
                    while len(row) < len(column_names):
                        row.append("")
                    completed_lines.append(row[:len(column_names)])
                
                df = pd.DataFrame(completed_lines, columns=column_names)
                tables.append(df)
                logger.info(f"Tableau heuristique créé: {len(df)} lignes, {len(df.columns)} colonnes")
    
    return tables

# Pour compatibilité
process_pdf_file = process_pdf_scanned_file