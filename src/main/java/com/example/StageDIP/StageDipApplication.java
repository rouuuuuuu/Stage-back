package com.example.StageDIP;

import com.github.javafaker.Faker;
import com.example.StageDIP.model.Fournisseur;
import com.example.StageDIP.model.Produit;
import com.example.StageDIP.repository.FournisseurRepository;

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
    @Profile("dev")  // Make sure it only runs in dev mode, don’t mess up prod, got it?
    public CommandLineRunner loadData(FournisseurRepository fournisseurRepository) {
        return args -> {
            Faker faker = new Faker();

            for (int i = 0; i < 20; i++) {
                Fournisseur f = new Fournisseur();
                f.setNom(faker.company().name());
                f.setNotation(faker.number().randomDouble(1, 1, 5));

                Set<Produit> produits = new HashSet<>();
                int productCount = faker.number().numberBetween(1, 5);
                for (int j = 0; j < productCount; j++) {
                    Produit p = new Produit();
                    p.setNom(faker.commerce().productName());
                    p.setCategorie(faker.commerce().department());
                    p.setPrixUnitaire(faker.number().randomDouble(2, 10, 1000));
                    p.setFournisseur(f);
                    produits.add(p);
                }
                f.setProduits(produits);

                fournisseurRepository.save(f);
            }
            System.out.println("Database populated with fake fournisseurs and produits");
        };
    }
}
