package com.supplychainx.service_livraison.model;

import com.supplychainx.service_livraison.model.enums.OrderClientStatus;
import com.supplychainx.service_production.model.Product;
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
@Table(name = "client_orders")
public class ClientOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_order_id", nullable = false)
    private Long clientOrderId;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @OneToMany(mappedBy = "clientOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProductClient> orderProducts = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderClientStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "total_amount")
    private Double totalAmount;

}
