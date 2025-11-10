package com.supplychainx.service_livraison.model;

import com.supplychainx.service_livraison.model.enums.LivraisonStatus;
import com.supplychainx.util.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "livraisons")
public class Livraison extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "livraison_id", nullable = false)
    private Long livraisonId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_order_id", nullable = false)
    private ClientOrder clientOrder;

    @Column(name = "vehicule", nullable = false)
    private String vehicule;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LivraisonStatus status;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Column(name = "delivery_date", nullable = false)
    private LocalDateTime deliveryDate;

}
