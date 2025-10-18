import logging
import pandas as pd
from typing import Dict, Any, List
import unicodedata
import os

# Configuration des logs
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("excel_processor.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

def process_excel_file(file_path: str) -> Dict[str, Any]:
    """
    Lit un fichier Excel, extrait le texte et les tableaux de chaque feuille.
    
    Args:
        file_path (str): Chemin vers le fichier Excel (.xlsx ou .xls).
    
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
        if ext not in ('.xlsx', '.xls'):
            logger.error(f"Format non supporté: {ext}")
            raise ValueError(f"Format non supporté: {ext}")
        
        logger.info(f"Traitement du fichier Excel: {file_path}")
        
        # Initialisation des résultats
        text_parts = []
        tables = []
        
        # Lire le fichier Excel
        with pd.ExcelFile(file_path) as xls:
            for sheet_name in xls.sheet_names:
                logger.info(f"Traitement de la feuille: {sheet_name}")
                try:
                    # Lire la feuille comme DataFrame
                    df = pd.read_excel(xls, sheet_name=sheet_name)
                    
                    # Nettoyer et valider le tableau
                    df_cleaned = _clean_table(df)
                    if _validate_table(df_cleaned):
                        tables.append(df_cleaned)
                        logger.info(f"Tableau extrait de la feuille {sheet_name}: {len(df_cleaned)} lignes, {len(df_cleaned.columns)} colonnes")
                    else:
                        logger.info(f"Tableau de la feuille {sheet_name} rejeté: validation échouée")
                    
                    # Extraire le texte de la feuille
                    sheet_text = _extract_text_from_df(df_cleaned)
                    if sheet_text:
                        text_parts.append(f"Feuille {sheet_name}:\n{sheet_text}")
                        logger.info(f"Texte extrait de la feuille {sheet_name}: {len(sheet_text)} caractères")
                    else:
                        logger.info(f"Aucun texte extrait de la feuille {sheet_name}")
                
                except Exception as e:
                    logger.warning(f"Erreur lors du traitement de la feuille {sheet_name}: {e}")
                    continue
        
        # Combiner le texte de toutes les feuilles
        full_text = '\n'.join(text_parts) if text_parts else ""
        
        logger.info(f"Traitement terminé pour {file_path}: {len(tables)} tableaux extraits, {len(full_text)} caractères")
        
        return {
            'type': 'excel',
            'text': full_text,
            'tables': tables
        }
    
    except Exception as e:
        logger.error(f"Erreur lors du traitement du fichier Excel: {e}")
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

def _extract_text_from_df(df: pd.DataFrame) -> str:
    """
    Extrait le texte d'un DataFrame en concaténant les cellules non vides.
    
    Args:
        df (pd.DataFrame): DataFrame source.
    
    Returns:
        str: Texte extrait.
    """
    if df.empty:
        return ""
    
    text_parts = []
    seen = set()
    for _, row in df.iterrows():
        row_text = ' '.join(str(cell).strip() for cell in row if str(cell).strip())
        if row_text and row_text not in seen:
            text_parts.append(row_text)
            seen.add(row_text)
    
    return '\n'.join(text_parts)

if __name__ == "__main__":
    print("=== EXCEL PROCESSOR ===")
    try:
        file_path = input("\nEntrez le chemin du fichier Excel: ").strip('"')
        result = process_excel_file(file_path)
        
        print(f"\n📄 Type: {result['type'].upper()}")
        print(f"\n📝 Texte ({len(result['text'])} caractères):")
        print(result['text'][:2000] + "..." if len(result['text']) > 2000 else result['text'])
        
        if result['tables']:
            print(f"\n📊 {len(result['tables'])} tableaux extraits:")
            for i, table in enumerate(result['tables'], 1):
                print(f"\nTableau {i} ({len(table)} lignes):")
                print(table.to_string(index=False))
                if len(table) > 3:
                    print("...")
        else:
            print("\n📊 Aucun tableau extrait.")
    
    except Exception as e:
        print(f"\n❌ Erreur: {e}")
        logger.exception("Erreur critique")
    finally:
        print("\n=== FIN DU TRAITEMENT ===")