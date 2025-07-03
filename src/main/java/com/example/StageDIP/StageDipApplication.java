package com.example.StageDIP;

import com.github.javafaker.Faker;
import com.example.StageDIP.model.Facture;
import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Offre;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.FactureRepository;
import com.example.StageDIP.repository.FournisseurRepository;
import com.example.StageDIP.repository.OffreRepository;
import com.example.StageDIP.repository.ProduitRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class StageDipApplication {

    public static void main(String[] args) {
        SpringApplication.run(StageDipApplication.class, args);
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner loadTestData(
        FournisseurRepository fournisseurRepo,
        ProduitRepository produitRepo,
        FactureRepository factureRepo,
        OffreRepository offreRepo
    ) {
    	
        return args -> {
            Faker faker = new Faker();

            for (int i = 0; i < 10; i++) {
                // 1. Créer fournisseur
                Fournisseur f = new Fournisseur();
                f.setNom(faker.company().name());
                f.setAdresse(faker.address().fullAddress());
                f.setEmail(faker.internet().emailAddress());
                f.setNotation(faker.number().randomDouble(2, 1, 5));
                f.setNumero(faker.number().randomDouble(0, 1000000000, 9999999999L));
                f.setFax(faker.number().randomDouble(0, 1000000, 9999999));
                fournisseurRepo.save(f);

                // 2. Créer les produits
                int nbProduits = faker.number().numberBetween(2, 5);
                Set<Produit> produitsDuFournisseur = new HashSet<>();
                for (int j = 0; j < nbProduits; j++) {
                    Produit p = new Produit();
                    p.setNom(faker.commerce().productName());
                    p.setCategorie(faker.commerce().department());
                    p.setPrixUnitaire(faker.number().randomDouble(2, 5, 500));
                    p.setFournisseur(f);
                    produitRepo.save(p);
                    produitsDuFournisseur.add(p);
                }
                f.setProduits(produitsDuFournisseur);
                fournisseurRepo.save(f);

                // 3. Créer factures et assigner des produits
                int nbFactures = faker.number().numberBetween(1, 3);
                for (int k = 0; k < nbFactures; k++) {
                    Facture currentFacture = new Facture();
                    currentFacture.setDate(faker.date().past(180, java.util.concurrent.TimeUnit.DAYS));
                    currentFacture.setMontantTotal(faker.number().randomDouble(2, 100, 10000));
                    currentFacture.setDelaiLivraison(faker.number().numberBetween(1, 30));
                    currentFacture.setPrixproduit(faker.number().randomDouble(2, 10, 500));
                    currentFacture.setFournisseur(f);
                    currentFacture = factureRepo.save(currentFacture);

                    Set<Produit> produitsFacture = new HashSet<>();
                    int produitsDansFacture = faker.number().numberBetween(1, produitsDuFournisseur.size());
                    int index = 0;
                    for (Produit p : produitsDuFournisseur) {
                        if (index++ >= produitsDansFacture) break;
                        p.setFacture(currentFacture);
                        produitRepo.save(p);
                        produitsFacture.add(p);
                    }
                    currentFacture.setProduits(produitsFacture);
                    factureRepo.save(currentFacture);
                }

                // 4. Créer les offres
                int nbOffres = faker.number().numberBetween(1, 3);
                for (int l = 0; l < nbOffres; l++) {
                    Offre offre = new Offre();
                    offre.setDateoffre(faker.date().past(90, java.util.concurrent.TimeUnit.DAYS));
                    offre.setDatevalidite(faker.date().future(30, java.util.concurrent.TimeUnit.DAYS));
                    offre.setStatut(faker.options().option("accepté", "refusé"));
                    offre.setFournisseur(f);
                    offreRepo.save(offre);
                }
            }

            System.out.println("Données faker complètes injectées : fournisseurs, produits, factures, offres.");
        };
    }
}
