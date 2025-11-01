package com.supplychainx.service_approvisionnement.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "fournisseurs")
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fournisseur_id")
    private Long fournisseurId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_email", nullable = false, unique = true)
    private String contactEmail;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "leadTimeDays")
    private Integer leadTimeDays;

    @Column(name = "isActive", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "fournisseur", cascade = CascadeType.ALL)
    private List<CommandeFournisseur> commandeFournisseurs;

    @ManyToMany(mappedBy = "fournisseurs", cascade = CascadeType.MERGE)
    private List<MatierePremiere> matieresPremieres;

    @PrePersist
    private void OnCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    private void OnUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
