package com.supplychainx.integration.service_production.repository;

import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import com.supplychainx.service_approvisionnement.repository.MatierePremiereRepository;
import com.supplychainx.service_production.model.BillOfMaterial;
import com.supplychainx.service_production.model.Product;
import com.supplychainx.service_production.repository.BillOfMaterialRepository;
import com.supplychainx.service_production.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(
        locations = "classpath:application-test.properties",
        properties = {
                "spring.config.location=classpath:application-test.properties",
                "spring.config.name=application-test"
        }
)
public class BillOfMaterialRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BillOfMaterialRepository billOfMaterialRepository;
    @Autowired
    private MatierePremiereRepository matierePremiereRepository;

    @BeforeEach
    void setUp() {
        billOfMaterialRepository.deleteAll();
        productRepository.deleteAll();
        matierePremiereRepository.deleteAll();
    }

    private Product createAndSaveProduct(String name) {
        Product product = new Product();
        product.setName(name);
        product.setStock(500);
        product.setProductionTimeHours(48);
        product.setCostPerUnit(15.75);
        return productRepository.save(product);
    }

    private MatierePremiere createAndSaveMatiere(String name) {
        MatierePremiere mp = new MatierePremiere();
        mp.setName(name);
        mp.setStockQuantity(200);
        mp.setStockMinimum(50);
        mp.setUnit("kg");
        return matierePremiereRepository.save(mp);
    }

    private BillOfMaterial createBillOfMaterial(Product product, MatierePremiere matiere, int quantity) {
        BillOfMaterial bom = new BillOfMaterial();
        bom.setProduct(product);
        bom.setMatierePremiere(matiere);
        bom.setQuantity(quantity);
        return bom;
    }

    @Test
    void testSaveBillOfMaterial() {
        Product product = createAndSaveProduct("Produit Test");
        MatierePremiere matiere = createAndSaveMatiere("Matiere Premiere Test");

        BillOfMaterial bom = createBillOfMaterial(product, matiere, 100);
        BillOfMaterial savedBOM = billOfMaterialRepository.save(bom);

        assert savedBOM.getBomId() != null;
        assert savedBOM.getProduct().getProductId().equals(product.getProductId());
    }

    @Test
    void testFindBillOfMaterialById() {
        Product product = createAndSaveProduct("Produit Test");
        MatierePremiere matiere = createAndSaveMatiere("Matiere Premiere Test");

        BillOfMaterial bom = createBillOfMaterial(product, matiere, 100);
        BillOfMaterial savedBOM = billOfMaterialRepository.save(bom);

        BillOfMaterial foundBOM = billOfMaterialRepository.findById(savedBOM.getBomId()).orElse(null);
        assert foundBOM != null;
        assert foundBOM.getProduct().getProductId().equals(product.getProductId());
    }

    @Test
    void testDeleteBillOfMaterial() {
        Product product = createAndSaveProduct("Produit Test");
        MatierePremiere matiere = createAndSaveMatiere("Matiere Premiere Test");

        BillOfMaterial bom = createBillOfMaterial(product, matiere, 100);
        BillOfMaterial savedBOM = billOfMaterialRepository.save(bom);

        billOfMaterialRepository.deleteById(savedBOM.getBomId());
        BillOfMaterial foundBOM = billOfMaterialRepository.findById(savedBOM.getBomId()).orElse(null);
        assert foundBOM == null;
    }

    @Test
    void testUpdateBillOfMaterial() {
        Product product = createAndSaveProduct("Produit Test");
        MatierePremiere matiere = createAndSaveMatiere("Matiere Premiere Test");

        BillOfMaterial bom = createBillOfMaterial(product, matiere, 100);
        BillOfMaterial savedBOM = billOfMaterialRepository.save(bom);

        savedBOM.setQuantity(200);
        BillOfMaterial updatedBOM = billOfMaterialRepository.save(savedBOM);
        assert updatedBOM.getQuantity() == 200;
        assert updatedBOM.getProduct().getProductId().equals(product.getProductId());
    }

    @Test
    void testFindAllBillOfMaterials() {
        Product product1 = createAndSaveProduct("Produit Test 1");
        MatierePremiere matiere1 = createAndSaveMatiere("Matiere 1");
        BillOfMaterial bom1 = createBillOfMaterial(product1, matiere1, 100);
        billOfMaterialRepository.save(bom1);

        Product product2 = createAndSaveProduct("Produit Test 2");
        MatierePremiere matiere2 = createAndSaveMatiere("Matiere 2");
        BillOfMaterial bom2 = createBillOfMaterial(product2, matiere2, 150);
        billOfMaterialRepository.save(bom2);

        var allBOMs = billOfMaterialRepository.findAll();
        assert allBOMs.size() == 2;
    }

}
