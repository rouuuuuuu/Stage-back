import logging
import pandas as pd
from typing import Dict, Any, List
import unicodedata
import os
import docx  # Nécessite python-docx : pip install python-docx

# Configuration des logs
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("word_processor.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

def process_word_file(file_path: str) -> Dict[str, Any]:
    """
    Lit un fichier Word, extrait le texte et les tableaux.
    
    Args:
        file_path (str): Chemin vers le fichier Word (.docx ou .doc).
    
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
        if ext not in ('.docx', '.doc'):
            logger.error(f"Format non supporté: {ext}")
            raise ValueError(f"Format non supporté: {ext}")
        
        logger.info(f"Traitement du fichier Word: {file_path}")
        
        # Lire le fichier Word
        doc = docx.Document(file_path)
        
        # Extraire le texte
        text = _extract_text_from_doc(doc)
        logger.info(f"Texte extrait: {len(text)} caractères")
        
        # Extraire les tableaux
        tables = []
        for table_num, table in enumerate(doc.tables, 1):
            logger.info(f"Traitement du tableau {table_num}")
            df = _table_to_df(table)
            df_cleaned = _clean_table(df)
            if _validate_table(df_cleaned):
                tables.append(df_cleaned)
                logger.info(f"Tableau {table_num} extrait: {len(df_cleaned)} lignes, {len(df_cleaned.columns)} colonnes")
            else:
                logger.info(f"Tableau {table_num} rejeté: validation échouée")
        
        logger.info(f"Traitement terminé pour {file_path}: {len(tables)} tableaux extraits")
        
        return {
            'type': 'word',
            'text': text,
            'tables': tables
        }
    
    except Exception as e:
        logger.error(f"Erreur lors du traitement du fichier Word: {e}")
        raise

def _extract_text_from_doc(doc: docx.Document) -> str:
    """
    Extrait le texte d'un document Word en concaténant les paragraphes non vides.
    
    Args:
        doc (docx.Document): Document Word.
    
    Returns:
        str: Texte extrait.
    """
    text_parts = []
    seen = set()
    for para in doc.paragraphs:
        para_text = para.text.strip()
        if para_text and para_text not in seen:
            text_parts.append(para_text)
            seen.add(para_text)
    return '\n'.join(text_parts)

def _table_to_df(table: docx.table.Table) -> pd.DataFrame:
    """
    Convertit un tableau Word en DataFrame pandas.
    
    Args:
        table (docx.table.Table): Tableau Word.
    
    Returns:
        pd.DataFrame: DataFrame représentant le tableau.
    """
    data = []
    for row in table.rows:
        row_data = [cell.text.strip() for cell in row.cells]
        data.append(row_data)
    # Utiliser la première ligne comme en-tête si elle existe
    if data:
        return pd.DataFrame(data[1:], columns=data[0] if data[0] else None)
    return pd.DataFrame()

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