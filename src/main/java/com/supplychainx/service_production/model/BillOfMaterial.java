package com.supplychainx.service_production.model;

import com.supplychainx.service_approvisionnement.model.MatierePremiere;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bill_of_materials")
public class BillOfMaterial {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    @Column(name = "bom_id", nullable = false)
    private Long bomId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_premiere_id", nullable = false)
    private MatierePremiere matierePremiere;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    private void OnCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    public void OnUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
