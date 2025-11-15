package com.supplychainx.integration.service_approvisionnement.repository;

import com.supplychainx.service_approvisionnement.model.Fournisseur;
import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class MatierePremiereRepositoryTest {

    @Autowired
    private MatierePremiereRepository matierePremiereRepository;
    @Autowired
    private FournisseurRepository fournisseurRepository;

    @BeforeEach
    public void setUp() {
        matierePremiereRepository.deleteAll();
        fournisseurRepository.deleteAll();
    }

    private MatierePremiere createMatierePremiere() {
        MatierePremiere matierePremiere = new MatierePremiere();
        matierePremiere.setName("Acier");
        matierePremiere.setFournisseurs(new ArrayList<>(List.of(createFournisseur())));
        matierePremiere.setStockMinimum(100);
        matierePremiere.setStockQuantity(500);
        matierePremiere.setUnit("kg");
        matierePremiere.setCreatedAt(LocalDateTime.now());
        matierePremiere.setUpdatedAt(LocalDateTime.now());
        return matierePremiere;
    }

    private Fournisseur createFournisseur() {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setName("Fournisseur Test");
        fournisseur.setIsActive(true);
        fournisseur.setContactEmail("fr@gmail.ma");
        fournisseur.setPhoneNumber("0606060606");
        fournisseur.setAddress("Casablanca, Morocco");
        fournisseur.setRating(4.5);
        fournisseur.setLeadTimeDays(7);
        return fournisseur;
    }

    @Test
    void testSave() {
        MatierePremiere matierePremiere = createMatierePremiere();
        matierePremiereRepository.save(matierePremiere);
        assert (matierePremiere.getMatierePremiereId() != null);
        assert (matierePremiere.getName().equals("Acier"));
        assert (matierePremiere.getStockQuantity() == 500);
    }

    @Test
    void testfindByStockMinimumLessOrEqual() {
        MatierePremiere matierePremiere = createMatierePremiere();
        matierePremiere.getFournisseurs().replaceAll(f -> fournisseurRepository.save(f));
        matierePremiereRepository.save(matierePremiere);
        var page = matierePremiereRepository.findByStockMinimumLessOrEqual(150, PageRequest.of(0, 10));
        assert (page.getTotalElements() == 1);
    }

    @Test
    void testDelete() {
        MatierePremiere matierePremiere = createMatierePremiere();
        matierePremiereRepository.save(matierePremiere);
        Long id = matierePremiere.getMatierePremiereId();
        matierePremiereRepository.delete(matierePremiere);
        var found = matierePremiereRepository.findById(id);
        assert (found.isEmpty());
    }

    @Test
    void testUpdate() {
        MatierePremiere matierePremiere = createMatierePremiere();
        matierePremiereRepository.save(matierePremiere);
        Long id = matierePremiere.getMatierePremiereId();
        matierePremiere.setMatierePremiereId(id);
        matierePremiere.setName("Acier1");
        matierePremiere.setFournisseurs(new ArrayList<>(List.of(createFournisseur())));
        matierePremiere.setStockMinimum(200);
        matierePremiere.setStockQuantity(300);
        matierePremiere.setUnit("L");
        matierePremiere.setCreatedAt(LocalDateTime.now());
        matierePremiere.setUpdatedAt(LocalDateTime.now());
        matierePremiereRepository.save(matierePremiere);
        var updated = matierePremiereRepository.findById(id).orElseThrow(() -> new RuntimeException("MatierePremiere not found"));
        assert (updated.getStockQuantity() == 300);
    }

    @Test
    void testFindById() {
        MatierePremiere matierePremiere = createMatierePremiere();
        matierePremiereRepository.save(matierePremiere);
        Long id = matierePremiere.getMatierePremiereId();
        var found = matierePremiereRepository.findById(id).orElseThrow(() -> new RuntimeException("MatierePremiere not found"));
        assert (found.getName().equals("Acier"));
    }

    @Test
    void testFindAll() {
        MatierePremiere matierePremiere1 = createMatierePremiere();
        matierePremiere1.getFournisseurs().replaceAll(f -> fournisseurRepository.save(f));

        MatierePremiere matierePremiere2 = createMatierePremiere();
        matierePremiere2.setName("Aluminium");
        // email unique pour respecter la contrainte
        matierePremiere2.getFournisseurs().forEach(f -> f.setContactEmail("fr2@gmail.ma"));
        matierePremiere2.getFournisseurs().replaceAll(f -> fournisseurRepository.save(f));

        matierePremiereRepository.save(matierePremiere1);
        matierePremiereRepository.save(matierePremiere2);
        var allMatieres = matierePremiereRepository.findAll();
        assert (allMatieres.size() == 2);
    }
}
