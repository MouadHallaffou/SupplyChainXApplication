package com.supplychainx.service_production.model;

import com.supplychainx.service_livraison.model.OrderProductClient;
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
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "proction_time_hours", nullable = false)
    private Integer productionTimeHours;

    @Column(name = "cost_per_unit", nullable = false)
    private Double costPerUnit;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProductClient> orderProducts = new ArrayList<>();

}