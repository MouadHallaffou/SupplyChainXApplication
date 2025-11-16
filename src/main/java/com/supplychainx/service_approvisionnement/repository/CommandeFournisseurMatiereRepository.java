package com.supplychainx.service_approvisionnement.repository;

import com.supplychainx.service_approvisionnement.model.CommandeFournisseurMatiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeFournisseurMatiereRepository extends JpaRepository<CommandeFournisseurMatiere, Long> {
}
