package com.supplychainx.service_approvisionnement.model;

import com.supplychainx.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "matieres_premieres")
public class MatierePremiere extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matiere_premiere_id")
    private Long matierePremiereId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "stock_minimum", nullable = false)
    private Integer stockMinimum;

    @Column(name = "unit", nullable = false)
    private String unit;

    @OneToMany(mappedBy = "matierePremiere", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandeFournisseurMatiere> commandeFournisseurMatieres = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "matiere_fournisseur",
        joinColumns = @JoinColumn(name = "matiere_premiere_id"),
        inverseJoinColumns = @JoinColumn(name = "fournisseur_id")
    )
    private List<Fournisseur> fournisseurs = new ArrayList<>();

}
