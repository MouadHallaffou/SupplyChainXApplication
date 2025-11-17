package com.supplychainx.integration.service_approvisionnement.repository;

import com.supplychainx.service_approvisionnement.model.Fournisseur;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

@DataJpaTest
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)

public class FournisseurRepositoryTest {

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @BeforeEach
    public void setUp() {
        fournisseurRepository.deleteAll();
    }

    private Fournisseur createFournisseur() {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setName("Test Supplier");
        fournisseur.setIsActive(true);
        fournisseur.setContactEmail("fr@gmail.ma");
        fournisseur.setPhoneNumber("0606060606");
        fournisseur.setAddress("Casablanca, Morocco");
        fournisseur.setRating(4.5);
        fournisseur.setLeadTimeDays(7);
        fournisseur.setCreatedAt(LocalDateTime.now());
        fournisseur.setUpdatedAt(LocalDateTime.now());
        return fournisseur;
    }

    @Test
    void testSave() {
        Fournisseur fournisseur = createFournisseur();
        fournisseurRepository.save(fournisseur);
        assert (fournisseur.getFournisseurId() != null);
        assert (fournisseur.getName().equals("Test Supplier"));
        assert (fournisseur.getIsActive());
    }

    @Test
    void testFindByNameIgnoreCase() {
        Fournisseur fournisseur = createFournisseur();
        fournisseurRepository.save(fournisseur);
        var found = fournisseurRepository.findByNameIgnoreCase("test supplier");
        assert (found.isPresent());
        assert (found.get().getFournisseurId().equals(fournisseur.getFournisseurId()));
    }

    @Test
    void testDelete() {
        Fournisseur fournisseur = createFournisseur();
        fournisseurRepository.save(fournisseur);
        Long id = fournisseur.getFournisseurId();
        fournisseurRepository.delete(fournisseur);
        var found = fournisseurRepository.findById(id);
        assert (found.isEmpty());
    }

    @Test
    void testUpdate() {
        Fournisseur fournisseur = createFournisseur();
        fournisseurRepository.save(fournisseur);
        Long id = fournisseur.getFournisseurId();

        fournisseur.setName("Updated Supplier");
        fournisseur.setIsActive(false);
        fournisseurRepository.save(fournisseur);

        var updated = fournisseurRepository.findById(id);
        assert (updated.isPresent());
        assert (updated.get().getName().equals("Updated Supplier"));
        assert (!updated.get().getIsActive());
    }

    @Test
    void testFindById() {
        Fournisseur fournisseur = createFournisseur();
        fournisseurRepository.save(fournisseur);
        Long id = fournisseur.getFournisseurId();

        var found = fournisseurRepository.findById(id);
        assert (found.isPresent());
        assert (found.get().getFournisseurId().equals(id));
    }

    @Test
    void testFindAll() {
        Fournisseur fournisseur1 = createFournisseur();
        fournisseurRepository.save(fournisseur1);
        Fournisseur fournisseur2 = createFournisseur();
        fournisseur2.setName("Another Supplier");
        fournisseur2.setContactEmail("fr2@gmail.com");
        fournisseurRepository.save(fournisseur2);

        var allFournisseurs = fournisseurRepository.findAll();
        assert (allFournisseurs.size() >= 2);
    }

}
