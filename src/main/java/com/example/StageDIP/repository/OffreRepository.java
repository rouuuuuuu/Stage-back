package com.example.StageDIP.repository;

import com.example.StageDIP.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    // Tu veux ajouter des méthodes custom plus tard ? Genre findByStatut ? T'as la base ici.
}
