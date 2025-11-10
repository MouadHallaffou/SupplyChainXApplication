package com.supplychainx.service_approvisionnement.model;

import com.supplychainx.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fournisseurs")
public class Fournisseur extends BaseEntity {

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

    @OneToMany(mappedBy = "fournisseur", cascade = CascadeType.ALL)
    private List<CommandeFournisseur> commandeFournisseurs;

    @ManyToMany(mappedBy = "fournisseurs", cascade = CascadeType.MERGE)
    private List<MatierePremiere> matieresPremieres;

}
