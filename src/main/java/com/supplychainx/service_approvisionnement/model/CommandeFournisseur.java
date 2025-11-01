package com.supplychainx.service_approvisionnement.model;

import com.supplychainx.service_approvisionnement.model.enums.FournisseurOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "commande_fournisseur")
public class CommandeFournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_fournisseur_id")
    private Long orderFournisseurId;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FournisseurOrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    @OneToMany(mappedBy = "commandeFournisseur", cascade = CascadeType.ALL)
    private List<CommandeFournisseurMatiere> commandeFournisseurMatieres;

    @PrePersist
    private void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = LocalDate.now();
        }
    }
}
