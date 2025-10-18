import pdfplumber
import pandas as pd
import os
from xlsxwriter import Workbook
from PIL import Image
import pytesseract
import cv2
import numpy as np

def extract_pdf_with_layout(pdf_path, output_folder="output"):
    """Extrait le contenu PDF en conservant la mise en page exacte"""
    
    os.makedirs(output_folder, exist_ok=True)
    full_text = ""
    tables_list = []
    
    with pdfplumber.open(pdf_path) as pdf:
        for i, page in enumerate(pdf.pages):
            # 1. Extraction texte avec positionnement
            text = page.extract_text(x_tolerance=1, y_tolerance=1) or ""
            full_text += text + "\n\n"
            with open(f"{output_folder}/page_{i+1}_text.txt", "w", encoding="utf-8") as f:
                f.write(text)
            
            # 2. Extraction des tableaux avec différentes stratégies
            # Stratégie 1: Tableaux avec lignes visibles
            table_settings_lines = {
                "vertical_strategy": "lines_strict", 
                "horizontal_strategy": "lines_strict",
                "intersection_y_tolerance": 5
            }
            
            # Stratégie 2: Tableaux basés sur le texte
            table_settings_text = {
                "vertical_strategy": "text", 
                "horizontal_strategy": "text",
                "text_x_tolerance": 5,
                "text_y_tolerance": 5,
                "intersection_y_tolerance": 10
            }
            
            # Essayer les deux stratégies
            strategies = [
                ("Lignes visibles", table_settings_lines),
                ("Texte", table_settings_text)
            ]
            
            all_tables = []
            for strategy_name, settings in strategies:
                try:
                    tables = page.find_tables(settings)
                    for table in tables:
                        table_data = table.extract()
                        if table_data and len(table_data) > 0:
                            # Vérifier que le tableau n'est pas déjà ajouté
                            df = pd.DataFrame(table_data)
                            df = df.map(lambda x: clean_cell(x))
                            
                            # Vérifier si le tableau est suffisamment différent des autres
                            is_duplicate = False
                            for existing_df in all_tables:
                                if df.equals(existing_df):
                                    is_duplicate = True
                                    break
                            
                            if not is_duplicate and len(df) > 0 and len(df.columns) > 0:
                                all_tables.append(df)
                                tables_list.append(df)
                                
                                # Export Excel avec mise en forme
                                excel_path = f"{output_folder}/page_{i+1}_table_{len(tables_list)}.xlsx"
                                with pd.ExcelWriter(excel_path, engine='xlsxwriter') as writer:
                                    df.to_excel(writer, index=False, sheet_name='Tableau')
                                    
                                    workbook = writer.book
                                    worksheet = writer.sheets['Tableau']
                                    
                                    # Ajout des bordures
                                    border_format = workbook.add_format({'border': 1})
                                    
                                    # Appliquer les bordures à toutes les cellules
                                    max_row, max_col = df.shape
                                    worksheet.conditional_format(
                                        0, 0, max_row, max_col-1,
                                        {'type': 'no_errors', 'format': border_format}
                                    )
                except Exception as e:
                    continue  # Continuer avec la stratégie suivante
            
            # 3. Rendu visuel pour vérification
            try:
                im = page.to_image(resolution=150)
                im.save(f"{output_folder}/page_{i+1}_visual.png")
            except Exception as e:
                print(f"Erreur lors de la génération de l'image pour la page {i+1}: {e}")
            
            # 4. Extraction texte bilingue (si nécessaire)
            try:
                if page.chars and any(ord(char['text']) > 127 for char in page.chars):
                    arabic_text = extract_arabic_text(page)
                    if arabic_text and len(arabic_text.strip()) > 10:
                        with open(f"{output_folder}/page_{i+1}_arabic.txt", "w", encoding="utf-8") as f:
                            f.write(arabic_text)
                        full_text += "\n\n=== TEXTE OCR ===\n" + arabic_text
            except Exception as e:
                print(f"Erreur OCR pour la page {i+1}: {e}")

    # Retourne le résultat structuré attendu par mai.py
    return {
        "type": "pdf_numeric",
        "text": full_text.strip(),
        "tables": tables_list
    }

def clean_cell(text):
    """Nettoie une cellule en conservant les sauts de ligne"""
    if pd.isna(text):
        return ""
    text = str(text)
    # Conserve les retours à la ligne internes
    text = text.replace('\n', '↲')
    # Supprime les espaces superflus
    text = ' '.join(text.split())
    return text.replace('↲', '\n')

def extract_arabic_text(page):
    """Extrait spécifiquement le texte arabe avec OCR"""
    try:
        cropped = page.crop(page.bbox)
        im = cropped.to_image(resolution=300)
        img = cv2.cvtColor(np.array(im.original), cv2.COLOR_RGB2BGR)
        
        # Prétraitement pour l'arabe
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        thresh = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)[1]
        
        # OCR avec configuration arabe
        custom_config = r'--oem 3 --psm 6 -l ara+fra'
        text = pytesseract.image_to_string(thresh, config=custom_config)
        
        return text.strip()
    except Exception as e:
        return ""

# Pour compatibilité avec l'ancien nom
process_pdf_file = extract_pdf_with_layout

if __name__ == "__main__":
    pdf_path = input("Entrez le chemin du fichier PDF: ").strip('"')
    result = extract_pdf_with_layout(pdf_path)
    print(f"Traitement terminé. Résultats dans le dossier 'output'")
    print(f"Texte extrait ({len(result['text'])} caractères)")