package com.supplychainx.service_approvisionnement.repository;

import com.supplychainx.service_approvisionnement.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    Optional<Fournisseur> findByNameIgnoreCase(String name);
}
