package com.supplychainx.integration.service_approvisionnement.repository;

import com.supplychainx.service_approvisionnement.model.CommandeFournisseur;
import com.supplychainx.service_approvisionnement.model.Fournisseur;
import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.model.enums.FournisseurOrderStatus;
import com.supplychainx.service_approvisionnement.repository.CommandeFournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.FournisseurRepository;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
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
public class OrderRepositoryTest {

    @Autowired
    private FournisseurRepository fournisseurRepository;
    @Autowired
    private MatierePremiereRepository matierePremiereRepository;
    @Autowired
    private CommandeFournisseurRepository commandeFournisseurRepository;

    @BeforeEach
    public void setUp() {
        fournisseurRepository.deleteAll();
        matierePremiereRepository.deleteAll();
        commandeFournisseurRepository.deleteAll();
    }

    private Fournisseur createFournisseur() {
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setName("Fournisseur Test");
        fournisseur.setIsActive(true);
        fournisseur.setContactEmail("frs@gmail.go");
        fournisseur.setPhoneNumber("0606060606");
        fournisseur.setAddress("Casablanca, Morocco");
        fournisseur.setRating(4.5);
        fournisseur.setLeadTimeDays(7);
        return fournisseur;
    }
    private MatierePremiere createMatierePremiere() {
        MatierePremiere matierePremiere = new MatierePremiere();
        matierePremiere.setName("Acier");
        matierePremiere.setFournisseurs(new ArrayList<>(List.of(createFournisseur())));
        matierePremiere.setStockMinimum(100);
        matierePremiere.setStockQuantity(500);
        matierePremiere.setUnit("kg");
        return matierePremiere;
    }
    private CommandeFournisseur createCommandeFournisseur() {
        CommandeFournisseur commandeFournisseur = new CommandeFournisseur();
        commandeFournisseur.setFournisseur(createFournisseur());
        commandeFournisseur.setStatus(FournisseurOrderStatus.EN_ATTENTE);
        commandeFournisseur.setOrderDate(LocalDate.now());
        commandeFournisseur.setCommandeFournisseurMatieres(new ArrayList<>());
        return commandeFournisseur;
    }

    @Test
    void testSaveOrder() {
        CommandeFournisseur order = createCommandeFournisseur();
        Fournisseur persisted = fournisseurRepository.save(order.getFournisseur());
        order.setFournisseur(persisted);
        commandeFournisseurRepository.save(order);
        assert (order.getFournisseur().getFournisseurId() != null);
        assert (order.getStatus() == FournisseurOrderStatus.EN_ATTENTE);
    }

    @Test
    void testFindOrderById() {
        CommandeFournisseur order = createCommandeFournisseur();
        Fournisseur persisted = fournisseurRepository.save(order.getFournisseur());
        order.setFournisseur(persisted);
        CommandeFournisseur savedOrder = commandeFournisseurRepository.save(order);
        CommandeFournisseur fetchedOrder = commandeFournisseurRepository.findById(savedOrder.getOrderFournisseurId()).orElse(null);
        assert (fetchedOrder != null);
        assert (fetchedOrder.getOrderFournisseurId().equals(savedOrder.getOrderFournisseurId()));
    }

    @Test
    void testDeleteOrder() {
        CommandeFournisseur order = createCommandeFournisseur();
        Fournisseur persisted = fournisseurRepository.save(order.getFournisseur());
        order.setFournisseur(persisted);
        CommandeFournisseur savedOrder = commandeFournisseurRepository.save(order);
        Long id = savedOrder.getOrderFournisseurId();
        commandeFournisseurRepository.delete(savedOrder);
        var found = commandeFournisseurRepository.findById(id);
        assert (found.isEmpty());
    }

    @Test
    void testUpdateOrder() {
        CommandeFournisseur order = createCommandeFournisseur();
        Fournisseur persisted = fournisseurRepository.save(order.getFournisseur());
        order.setFournisseur(persisted);
        CommandeFournisseur savedOrder = commandeFournisseurRepository.save(order);
        savedOrder.setStatus(FournisseurOrderStatus.RECUE);
        CommandeFournisseur updatedOrder = commandeFournisseurRepository.save(savedOrder);
        assert (updatedOrder.getStatus() == FournisseurOrderStatus.RECUE);
    }

    @Test
    void testListAllOrders() {
        CommandeFournisseur order1 = createCommandeFournisseur();
        Fournisseur persisted1 = fournisseurRepository.save(order1.getFournisseur());
        order1.setFournisseur(persisted1);
        CommandeFournisseur order2 = createCommandeFournisseur();
        order2.getFournisseur().setContactEmail("frs2@gmail.com");
        Fournisseur persisted2 = fournisseurRepository.save(order2.getFournisseur());
        order2.setFournisseur(persisted2);
        commandeFournisseurRepository.save(order1);
        commandeFournisseurRepository.save(order2);
        List<CommandeFournisseur> orders = commandeFournisseurRepository.findAll();
        assert (orders.size() == 2);
    }

}
