package com.supplychainx.service_approvisionnement.repository;

import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatierePremiereRepository extends JpaRepository<MatierePremiere , Long> {
    @Query("SELECT m FROM MatierePremiere m WHERE m.stockMinimum <= :stockCritique")
    Page<MatierePremiere> findByStockMinimumLessOrEqual(int stockCritique,
                                                        Pageable pageable);

    boolean existsByNameIgnoreCase (String name);
}
