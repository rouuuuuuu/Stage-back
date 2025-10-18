import requests
import xml.etree.ElementTree as ET
import logging
import re
import pandas as pd
from datetime import datetime
import xml.dom.minidom as minidom

# Configuration des logs
logger = logging.getLogger(__name__)

def extract_invoice_data_for_xml(result: dict) -> dict:
    """
    Extrait les données de facture pour la génération XML.
    
    Args:
        result (dict): Résultat du traitement du PDF
        
    Returns:
        dict: Données formatées pour XML
    """
    from mai import extract_invoice_fields  # Import local pour éviter les imports circulaires
    
    xml_data = {}
    
    if 'pdf' in result['type']:
        # Extraire les champs de base
        xml_data = extract_invoice_fields(result['text'])
        
        # Ajouter les produits si des tableaux sont détectés
        produits = []
        for table in result.get('tables', []):
            # Vérifier si c'est un tableau de produits (basé sur les colonnes)
            if hasattr(table, 'columns') and len(table.columns) >= 4:
                # Convertir le DataFrame en liste de listes
                for _, row in table.iterrows():
                    row_list = []
                    for col in table.columns:
                        val = row[col] if pd.notna(row[col]) else ""
                        row_list.append(str(val))
                    if len(row_list) > 0 and (row_list[0] or row_list[1]):  # Au moins code ou description
                        produits.append(row_list)
        
        if produits:
            xml_data['produits'] = produits
            logger.info(f"{len(produits)} lignes de produits extraites pour XML")
    
    return xml_data

def generate_invoice_xml(invoice_data: dict, output_path: str = None) -> str:
    """
    Génère un fichier XML à partir des données extraites d'une facture.
    
    Args:
        invoice_data (dict): Données de la facture extraites
        output_path (str): Chemin pour sauvegarder le fichier XML (optionnel)
    
    Returns:
        str: Contenu XML généré
    """
    # Créer l'élément racine
    root = ET.Element("Facture")
    
    # Ajouter les éléments de base
    if 'numero_facture' in invoice_data and invoice_data['numero_facture']:
        ET.SubElement(root, "NumeroFacture").text = str(invoice_data['numero_facture'])
    
    if 'date_facturation' in invoice_data and invoice_data['date_facturation']:
        # Normaliser la date si nécessaire
        date_str = str(invoice_data['date_facturation'])
        # Si format JJ/MM/AAAA, convertir en AAAA-MM-JJ
        if re.match(r'\d{2}/\d{2}/\d{4}', date_str):
            parts = date_str.split('/')
            date_str = f"{parts[2]}-{parts[1]}-{parts[0]}"
        ET.SubElement(root, "DateFacturation").text = date_str
    
    if 'code_client' in invoice_data and invoice_data['code_client']:
        ET.SubElement(root, "CodeClient").text = str(invoice_data['code_client'])
    
    if 'nom_client' in invoice_data and invoice_data['nom_client']:
        ET.SubElement(root, "NomClient").text = str(invoice_data['nom_client'])
    
    if 'telephone' in invoice_data and invoice_data['telephone']:
        ET.SubElement(root, "Telephone").text = str(invoice_data['telephone'])
    
    if 'montant_total' in invoice_data and invoice_data['montant_total']:
        montant = str(invoice_data['montant_total'])
        # Nettoyer le montant (supprimer symboles, espaces)
        montant_clean = re.sub(r'[^\d,\.]', '', montant)
        # Remplacer virgule par point pour format numérique
        montant_clean = montant_clean.replace(',', '.')
        ET.SubElement(root, "MontantTotal").text = montant_clean
        ET.SubElement(root, "Devise").text = "EUR" if "€" in montant else "DT"
    
    # Ajouter les lignes de produits si disponibles
    if 'produits' in invoice_data and invoice_data['produits']:
        produits_elem = ET.SubElement(root, "Produits")
        for i, produit in enumerate(invoice_data['produits'], 1):
            produit_elem = ET.SubElement(produits_elem, "Produit")
            ET.SubElement(produit_elem, "Ligne").text = str(i)
            
            if len(produit) > 0:
                ET.SubElement(produit_elem, "Code").text = produit[0] if produit[0] else ""
            if len(produit) > 1:
                ET.SubElement(produit_elem, "Description").text = produit[1] if produit[1] else ""
            if len(produit) > 2:
                qté = produit[2] if produit[2] else "1"
                ET.SubElement(produit_elem, "Quantite").text = qté
            if len(produit) > 3:
                pu = re.sub(r'[^\d,\.]', '', produit[3]) if produit[3] else "0"
                pu = pu.replace(',', '.')
                ET.SubElement(produit_elem, "PrixUnitaire").text = pu
            if len(produit) > 5:
                total = re.sub(r'[^\d,\.]', '', produit[5]) if produit[5] else "0"
                total = total.replace(',', '.')
                ET.SubElement(produit_elem, "TotalLigne").text = total
    
    # Générer le XML avec indentation
    rough_string = ET.tostring(root, encoding='unicode')
    reparsed = minidom.parseString(rough_string)
    xml_content = reparsed.toprettyxml(indent="  ", encoding=None)
    
    # Sauvegarder dans un fichier si demandé
    if output_path:
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write('<?xml version="1.0" encoding="UTF-8"?>\n')
            f.write(xml_content)
        logger.info(f"Fichier XML généré: {output_path}")
    
    return '<?xml version="1.0" encoding="UTF-8"?>\n' + xml_content

def send_xml_to_springboot(xml_content: str, springboot_url: str) -> bool:
    """
    Envoie le contenu XML à un endpoint Spring Boot.
    
    Args:
        xml_content (str): Contenu XML à envoyer
        springboot_url (str): URL de l'endpoint Spring Boot
    
    Returns:
        bool: True si succès, False sinon
    """
    try:
        headers = {
            'Content-Type': 'application/xml',
            'Accept': 'application/xml'
        }
        
        logger.info(f"Envoi du XML à {springboot_url}")
        response = requests.post(springboot_url, data=xml_content.encode('utf-8'), headers=headers, timeout=30)
        
        if response.status_code in [200, 201]:
            logger.info(f"XML envoyé avec succès. Status: {response.status_code}")
            print(f"✅ XML envoyé avec succès à {springboot_url}")
            return True
        else:
            logger.error(f"Erreur lors de l'envoi du XML. Status: {response.status_code}, Response: {response.text}")
            print(f"❌ Erreur lors de l'envoi du XML: {response.status_code}")
            return False
            
    except Exception as e:
        logger.error(f"Exception lors de l'envoi du XML: {e}")
        print(f"❌ Exception lors de l'envoi du XML: {e}")
        return False

def process_and_send_invoice(result: dict, springboot_url: str = "http://localhost:8080/api/factures") -> bool:
    """
    Fonction principale qui extrait les données, génère le XML et l'envoie à Spring Boot.
    
    Args:
        result (dict): Résultat du traitement du PDF
        springboot_url (str): URL de l'endpoint Spring Boot
    
    Returns:
        bool: True si tout s'est bien passé, False sinon
    """
    try:
        print(f"\n{'='*50}")
        print("EXPORT XML POUR SPRING BOOT")
        print(f"{'='*50}")
        
        # Extraire les données pour XML
        xml_data = extract_invoice_data_for_xml(result)
        
        # Générer le XML
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        xml_filename = f"facture_{timestamp}.xml"
        xml_content = generate_invoice_xml(xml_data, xml_filename)
        
        print(f"📄 Fichier XML généré: {xml_filename}")
        print(f"\nContenu XML (500 premiers caractères):")
        print("-" * 50)
        print(xml_content[:500] + "..." if len(xml_content) > 500 else xml_content)
        
        # Envoyer à Spring Boot
        success = send_xml_to_springboot(xml_content, springboot_url)
        
        if success:
            print(f"✅ Succès: Données envoyées à Spring Boot")
        else:
            print(f"❌ Échec: Impossible d'envoyer les données à Spring Boot")
        
        return success
        
    except Exception as e:
        logger.error(f"Erreur dans process_and_send_invoice: {e}")
        print(f"❌ Erreur lors du traitement et de l'envoi: {e}")
        return False