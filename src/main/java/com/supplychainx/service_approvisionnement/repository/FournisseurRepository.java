package com.supplychainx.service_approvisionnement.repository;

import com.supplychainx.service_approvisionnement.model.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
}
