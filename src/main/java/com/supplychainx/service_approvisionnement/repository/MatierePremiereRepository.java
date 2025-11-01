package com.supplychainx.service_approvisionnement.repository;

import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatierePremiereRepository extends JpaRepository<MatierePremiere , Long> {
}
