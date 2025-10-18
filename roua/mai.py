import sys
import logging
import os
import pdfplumber
import re
import pandas as pd
import xml.etree.ElementTree as ET
from xml.dom import minidom
from datetime import datetime
from exeel import process_excel_file
from word import process_word_file
from cssv import process_csv_file
from pdfs import extract_pdf_with_layout
from pdfsc import process_pdf_scanned_file
from img import process_image_file

# Configuration des logs
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("main.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

def detect_pdf_type(file_path: str) -> str:
    """
    Détecte si un fichier PDF est numérique ou numérisé.
    
    Args:
        file_path (str): Chemin vers le fichier PDF.
    
    Returns:
        str: 'pdf_numeric' si le PDF contient du texte extractible, 'pdf_scanned' sinon.
    """
    try:
        with pdfplumber.open(file_path) as pdf:
            first_page = pdf.pages[0]
            text = first_page.extract_text()
            if text and len(text.strip()) > 50:  # Seuil arbitraire pour détecter du texte significatif
                return 'pdf_numeric'
            return 'pdf_scanned'
    except Exception as e:
        logger.warning(f"Erreur lors de la détection du type de PDF: {e}")
        return 'pdf_scanned'

def extract_invoice_fields(text):
    """Extrait les champs spécifiques d'une facture"""
    fields = {}
    
    # Numéro de facture
    facture_match = re.search(r'(\d{16})', text)
    if facture_match:
        fields['numero_facture'] = facture_match.group(1)
    
    # Date de facturation
    date_match = re.search(r'Date de Facturation\s+([\d/]+)', text)
    if date_match:
        fields['date_facturation'] = date_match.group(1)
    
    # Code client
    client_match = re.search(r'Code client\s+([^\s]+)', text)
    if client_match:
        fields['code_client'] = client_match.group(1)
    
    # Nom du client
    nom_match = re.search(r'REBII AHMED\s+REBII AHMED', text)
    if nom_match:
        fields['nom_client'] = 'REBII AHMED'
    
    # Montant total
    total_match = re.search(r'Montant total de la facture.*?([\d,]+)', text)
    if total_match:
        fields['montant_total'] = total_match.group(1)
    
    # Téléphone
    tel_match = re.search(r'Numéro Téléphonique\s+(\d+)', text)
    if tel_match:
        fields['telephone'] = tel_match.group(1)
    
    return fields

def display_table(table_df, table_num):
    """Affiche un tableau de manière formatée"""
    print(f"\n{'='*60}")
    print(f"TABLEAU {table_num}")
    print(f"{'='*60}")
    print(f"Dimensions: {table_df.shape[0]} lignes × {table_df.shape[1]} colonnes")
    print(f"{'='*60}")
    
    if not table_df.empty:
        # Calculer la largeur optimale pour chaque colonne
        col_widths = []
        for col in table_df.columns:
            max_len = max(len(str(col)), 15)
            for val in table_df[col].head(10):
                max_len = max(max_len, len(str(val)) if pd.notna(val) else 4)
            col_widths.append(min(max_len, 30))  # Max 30 caractères par colonne
        
        # Afficher les noms de colonnes
        headers = " | ".join([f"{str(col):<{col_widths[i]}}" for i, col in enumerate(table_df.columns)])
        print(headers)
        print("-" * len(headers))
        
        # Afficher les premières lignes (max 15)
        rows_to_show = min(15, len(table_df))
        for idx, row in table_df.head(rows_to_show).iterrows():
            row_str = " | ".join([f"{str(val):<{col_widths[i]}}" if pd.notna(val) else f"{'':<{col_widths[i]}}" 
                                for i, val in enumerate(row.values)])
            print(row_str)
        
        if len(table_df) > rows_to_show:
            print("...")
            print(f"(Affichage des {rows_to_show} premières lignes sur {len(table_df)} au total)")
    else:
        print("Tableau vide")

def display_invoice_summary(fields):
    """Affiche un résumé des champs de la facture"""
    if fields:
        print(f"\n{'='*50}")
        print("RÉSUMÉ DE LA FACTURE")
        print(f"{'='*50}")
        for key, value in fields.items():
            key_name = {
                'numero_facture': 'Numéro de facture',
                'date_facturation': 'Date de facturation',
                'code_client': 'Code client',
                'nom_client': 'Nom du client',
                'montant_total': 'Montant total (DT)',
                'telephone': 'Téléphone'
            }.get(key, key)
            print(f"{key_name:<20}: {value}")

# ==================== FONCTIONS D'EXPORT XML ====================

def extract_invoice_data_from_result(result):
    """
    Extrait les données de facture structurées à partir du résultat du traitement.
    """
    try:
        # Extraire les champs de base
        invoice_fields = result.get('invoice_fields', {})
        text = result.get('text', '')
        tables = result.get('tables', [])
        
        # Données de base
        invoice_data = {
            'numero_facture': invoice_fields.get('numero_facture', ''),
            'date_facturation': invoice_fields.get('date_facturation', ''),
            'code_client': invoice_fields.get('code_client', ''),
            'nom_client': invoice_fields.get('nom_client', ''),
            'telephone': invoice_fields.get('telephone', ''),
            'montant_total': invoice_fields.get('montant_total', ''),
            'lignes_produits': []
        }
        
        # Extraire les lignes de produits des tableaux
        for table in tables:
            if isinstance(table, pd.DataFrame):
                # Vérifier si c'est un tableau de produits (colonnes typiques)
                product_columns = ['Code', 'Description', 'Qté', 'PU HT', 'TVA', 'Total HT']
                if any(col in table.columns for col in product_columns):
                    # Convertir le DataFrame en liste de dictionnaires
                    for _, row in table.iterrows():
                        ligne_produit = {}
                        for col in product_columns:
                            if col in table.columns:
                                valeur = row[col] if pd.notna(row[col]) else ''
                                ligne_produit[col] = str(valeur).strip()
                        if any(ligne_produit.values()):  # Ne pas ajouter les lignes vides
                            invoice_data['lignes_produits'].append(ligne_produit)
        
        logger.info(f"Données de facture extraites: {len(invoice_data['lignes_produits'])} lignes de produits")
        return invoice_data
        
    except Exception as e:
        logger.error(f"Erreur lors de l'extraction des données de facture: {e}")
        return {
            'numero_facture': '',
            'date_facturation': '',
            'code_client': '',
            'nom_client': '',
            'telephone': '',
            'montant_total': '',
            'lignes_produits': []
        }

def create_invoice_xml(invoice_data):
    """
    Crée un fichier XML à partir des données de facture extraites.
    """
    try:
        # Créer l'élément racine
        root = ET.Element("facture")
        
        # Informations générales
        info_generales = ET.SubElement(root, "informationsGenerales")
        
        # Numéro de facture
        if invoice_data.get('numero_facture'):
            numero = ET.SubElement(info_generales, "numero")
            numero.text = str(invoice_data.get('numero_facture', ''))
        
        # Date de facturation
        if invoice_data.get('date_facturation'):
            date = ET.SubElement(info_generales, "dateFacturation")
            date.text = str(invoice_data.get('date_facturation', ''))
        
        # Client
        if invoice_data.get('nom_client') or invoice_data.get('code_client'):
            client = ET.SubElement(info_generales, "client")
            if invoice_data.get('nom_client'):
                nom = ET.SubElement(client, "nom")
                nom.text = str(invoice_data.get('nom_client', ''))
            if invoice_data.get('code_client'):
                code = ET.SubElement(client, "code")
                code.text = str(invoice_data.get('code_client', ''))
        
        # Contact
        if invoice_data.get('telephone'):
            contact = ET.SubElement(info_generales, "contact")
            telephone = ET.SubElement(contact, "telephone")
            telephone.text = str(invoice_data.get('telephone', ''))
        
        # Montant total
        if invoice_data.get('montant_total'):
            montant = ET.SubElement(info_generales, "montantTotal")
            montant.text = str(invoice_data.get('montant_total', ''))
        
        # Lignes de produits
        if invoice_data.get('lignes_produits'):
            produits = ET.SubElement(root, "produits")
            for i, ligne in enumerate(invoice_data.get('lignes_produits', [])):
                produit = ET.SubElement(produits, "produit")
                produit.set("id", str(i + 1))
                
                # Code produit
                if ligne.get('Code'):
                    code_prod = ET.SubElement(produit, "code")
                    code_prod.text = str(ligne.get('Code', ''))
                
                # Description
                if ligne.get('Description'):
                    desc = ET.SubElement(produit, "description")
                    desc.text = str(ligne.get('Description', ''))
                
                # Quantité
                if ligne.get('Qté'):
                    qte = ET.SubElement(produit, "quantite")
                    qte.text = str(ligne.get('Qté', ''))
                
                # Prix unitaire
                if ligne.get('PU HT'):
                    pu = ET.SubElement(produit, "prixUnitaire")
                    pu.text = str(ligne.get('PU HT', ''))
                
                # TVA
                if ligne.get('TVA'):
                    tva = ET.SubElement(produit, "tva")
                    tva.text = str(ligne.get('TVA', ''))
                
                # Total
                if ligne.get('Total HT'):
                    total = ET.SubElement(produit, "total")
                    total.text = str(ligne.get('Total HT', ''))
        
        # Convertir en chaîne XML formatée
        rough_string = ET.tostring(root, encoding='unicode')
        reparsed = minidom.parseString(rough_string)
        formatted_xml = reparsed.toprettyxml(indent="  ")
        
        # Supprimer les lignes vides
        lines = [line for line in formatted_xml.split('\n') if line.strip()]
        formatted_xml = '\n'.join(lines)
        
        logger.info("XML de facture créé avec succès")
        return formatted_xml
        
    except Exception as e:
        logger.error(f"Erreur lors de la création du XML: {e}")
        # Retourner un XML minimal en cas d'erreur
        return """<?xml version="1.0" ?>
<facture>
  <erreur>Erreur lors de la génération du XML</erreur>
</facture>"""

def save_xml_to_file(xml_content, filename=None):
    """
    Sauvegarde le contenu XML dans un fichier.
    """
    try:
        if filename is None:
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"facture_{timestamp}.xml"
        
        with open(filename, 'w', encoding='utf-8') as f:
            f.write(xml_content)
        
        logger.info(f"XML sauvegardé dans: {filename}")
        return filename
        
    except Exception as e:
        logger.error(f"Erreur lors de la sauvegarde du XML: {e}")
        raise

def main():
    """
    Point d'entrée principal pour le traitement des fichiers Excel, Word, CSV, PDF numérique, PDF numérisé et image.
    Demande le chemin du fichier à l'utilisateur, détecte le type de fichier,
    appelle le processeur approprié, et affiche les résultats.
    """
    print("=== DOCUMENT PROCESSOR ===")
    try:
        # Obtenir le chemin du fichier depuis l'argument ou l'entrée utilisateur
        file_path = sys.argv[1] if len(sys.argv) > 1 else input("\nEntrez le chemin du fichier (Excel, Word, CSV, PDF ou image): ").strip('"')
        
        # Vérifier l'existence du fichier
        if not os.path.exists(file_path):
            logger.error(f"Fichier introuvable: {file_path}")
            raise FileNotFoundError(f"Fichier introuvable: {file_path}")
        
        # Déterminer le type de fichier
        ext = os.path.splitext(file_path)[1].lower()
        if ext in ('.xlsx', '.xls'):
            file_type = 'excel'
            processor = process_excel_file
        elif ext in ('.docx', '.doc'):
            file_type = 'word'
            processor = process_word_file
        elif ext == '.csv':
            file_type = 'csv'
            processor = process_csv_file
        elif ext == '.pdf':
            file_type = detect_pdf_type(file_path)
            processor = extract_pdf_with_layout if file_type == 'pdf_numeric' else process_pdf_scanned_file
        elif ext in ('.png', '.jpg', '.jpeg', '.bmp', '.tiff'):
            file_type = 'image'
            processor = process_image_file
        else:
            logger.error(f"Format non supporté: {ext}")
            raise ValueError(f"Format non supporté: {ext}")
        
        logger.info(f"Début du traitement pour le fichier {file_type}: {file_path}")
        
        # Appeler le processeur approprié
        result = processor(file_path)
        
        # Vérifier que le résultat n'est pas None
        if result is None:
            logger.error("Le processeur n'a pas retourné de résultat")
            raise ValueError("Le processeur n'a pas retourné de résultat")
        
        # Ajouter les champs de facture au résultat pour l'export XML
        if 'pdf' in result['type']:
            result['invoice_fields'] = extract_invoice_fields(result['text'])
        
        # Afficher les résultats
        print(f"\n[TYPE] {result['type'].upper()}")
        
        # Pour les PDF, extraire les champs de facture
        if 'pdf' in result['type']:
            invoice_fields = result.get('invoice_fields', {})
            display_invoice_summary(invoice_fields)
        
        print(f"\n[TEXTE] ({len(result['text'])} caractères):")
        print(result['text'][:2000] + "..." if len(result['text']) > 2000 else result['text'])
        
        if result['tables']:
            print(f"\n[TABLEAUX] {len(result['tables'])} tableaux extraits:")
            for i, table in enumerate(result['tables'], 1):
                display_table(table, i)
        else:
            print("\n[TABLEAUX] Aucun tableau extrait.")
        
        # Exporter en XML
        try:
            print(f"\n[EXPORT XML] Génération du fichier XML...")
            invoice_data = extract_invoice_data_from_result(result)
            xml_content = create_invoice_xml(invoice_data)
            
            # Sauvegarder le XML
            xml_filename = save_xml_to_file(xml_content)
            print(f"[EXPORT XML] Fichier XML sauvegardé: {xml_filename}")
            
            # Afficher un aperçu du XML
            print(f"\n[APERÇU XML] 500 premiers caractères:")
            print(xml_content[:500] + "..." if len(xml_content) > 500 else xml_content)
            
        except Exception as e:
            print(f"[EXPORT XML] Erreur lors de l'export XML: {e}")
            logger.error(f"Erreur lors de l'export XML: {e}")
    
    except Exception as e:
        print(f"\n[ERREUR] {e}")
        logger.exception("Erreur critique")
    finally:
        print("\n=== FIN DU TRAITEMENT ===")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        print("\nInterrompu par l'utilisateur")
        logger.info("Programme interrompu par l'utilisateur")
    except Exception as e:
        logger.critical(f"ERREUR: {e}")
        print("Une erreur critique est survenue - voir le fichier log")