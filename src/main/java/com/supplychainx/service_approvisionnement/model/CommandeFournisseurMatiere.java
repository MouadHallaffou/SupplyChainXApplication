package com.supplychainx.service_approvisionnement.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "commande_fournisseur_matiere")
public class CommandeFournisseurMatiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_fournisseur_id", nullable = false)
    private CommandeFournisseur commandeFournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matiere_premiere_id", nullable = false)
    private MatierePremiere matierePremiere;

}
