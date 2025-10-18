import logging
import pandas as pd
from typing import Dict, Any, List
import unicodedata
import os
import pytesseract
from PIL import Image, ImageEnhance, UnidentifiedImageError

# Configuration OCR
pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

# Configuration des logs
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("image_processor.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

def process_image_file(file_path: str) -> Dict[str, Any]:
    """
    Lit un fichier image, extrait le texte et les tableaux via OCR.
    
    Args:
        file_path (str): Chemin vers le fichier image (.png, .jpg, .jpeg, .bmp, .tiff).
    
    Returns:
        Dict[str, Any]: Dictionnaire contenant le type de fichier, le texte extrait,
                       et la liste des tableaux sous forme de DataFrames.
    
    Raises:
        FileNotFoundError: Si le fichier n'existe pas.
        ValueError: Si le format du fichier n'est pas supporté.
        UnidentifiedImageError: Si l'image ne peut pas être ouverte.
        Exception: Pour les erreurs générales lors du traitement.
    """
    try:
        # Vérifier l'existence et le format du fichier
        if not os.path.exists(file_path):
            logger.error(f"Fichier introuvable: {file_path}")
            raise FileNotFoundError(f"Fichier introuvable: {file_path}")
        
        ext = os.path.splitext(file_path)[1].lower()
        supported_image_exts = ('.png', '.jpg', '.jpeg', '.bmp', '.tiff')
        if ext not in supported_image_exts:
            logger.error(f"Format non supporté: {ext}")
            raise ValueError(f"Format non supporté: {ext}")
        
        logger.info(f"Traitement du fichier image: {file_path}")
        
        # Lire et pré-traiter l'image
        try:
            img = Image.open(file_path).convert('L')  # Convertir en niveaux de gris
            img = ImageEnhance.Contrast(img).enhance(2.0)  # Augmenter le contraste
        except UnidentifiedImageError as e:
            logger.error(f"Format d'image non reconnu: {file_path}")
            raise UnidentifiedImageError(f"Format d'image non reconnu: {file_path}") from e
        
        # Extraire le texte via OCR
        text = pytesseract.image_to_string(img, lang='fra+eng+ara', config='--psm 6')
        if text.strip():
            text = _clean_text(text)
            logger.info(f"Texte extrait: {len(text)} caractères")
        else:
            text = ""
            logger.info("Aucun texte extrait")
        
        # Extraire les tableaux via OCR
        tables = []
        table_data = pytesseract.image_to_data(img, lang='fra+eng+ara', config='--psm 6', output_type=pytesseract.Output.DATAFRAME)
        if not table_data.empty:
            df = _process_tesseract_table(table_data)
            if _validate_table(df):
                tables.append(df)
                logger.info(f"Tableau extrait: {len(df)} lignes, {len(df.columns)} colonnes")
            else:
                logger.info("Tableau OCR rejeté: validation échouée")
        
        logger.info(f"Traitement terminé pour {file_path}: {len(tables)} tableaux extraits, {len(text)} caractères")
        
        return {
            'type': 'image',
            'text': text,
            'tables': tables
        }
    
    except Exception as e:
        logger.error(f"Erreur lors du traitement du fichier image: {e}")
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
    df = df[df.apply(lambda x: x.str.strip().str.len().sum() > 0, axis=1)]
    
    # Nettoyer chaque cellule
    for col in df.columns:
        df[col] = df[col].apply(_clean_cell)
    
    # Supprimer la première colonne si elle est entièrement vide
    if len(df.columns) > 1 and df.iloc[:, 0].str.strip().eq('').all():
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
    
    header = df.iloc[0]
    if header.str.strip().eq('').all() or header.str.isnumeric().all():
        logger.info("Tableau rejeté: en-tête vide ou numérique")
        return False
    
    # Vérifier la densité des cellules non vides
    non_empty_cells = df.apply(lambda x: x.str.strip().ne('').sum(), axis=1)
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
    
    Args:
        data (pd.DataFrame): Données OCR issues de pytesseract.image_to_data.
    
    Returns:
        pd.DataFrame: DataFrame représentant le tableau détecté.
    """
    data = data[data['text'].notnull() & (data['text'].str.strip() != '')].copy()
    if data.empty:
        logger.info("Aucun tableau détecté via pytesseract: données vides")
        return pd.DataFrame()
    
    # Grouper par lignes et colonnes approximatives
    data.loc[:, 'line'] = (data['top'] // 10).astype(int)
    data.loc[:, 'column'] = (data['left'] // 50).astype(int)
    
    table = []
    for line_num, line_group in data.groupby('line'):
        row = []
        for col_num, cell_group in line_group.groupby('column'):
            cell_text = ' '.join(cell_group['text'].str.strip())
            row.append(cell_text)
        if len(row) >= 2:
            table.append(row)
    
    if not table or len(table) < 2:
        logger.info("Aucun tableau détecté via pytesseract: moins de 2 lignes ou colonnes")
        return pd.DataFrame()
    
    max_cols = max(len(row) for row in table)
    df = pd.DataFrame([row + [''] * (max_cols - len(row)) for row in table])
    return df