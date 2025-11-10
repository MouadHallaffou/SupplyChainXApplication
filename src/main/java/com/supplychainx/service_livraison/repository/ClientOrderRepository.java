package com.supplychainx.service_livraison.repository;

import com.supplychainx.service_livraison.model.ClientOrder;
import com.supplychainx.service_livraison.model.enums.ClientOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientOrderRepository extends JpaRepository<ClientOrder, Long> {

    Page<ClientOrder> findByStatus(ClientOrderStatus status, Pageable pageable);

    @Query("SELECT co FROM ClientOrder co LEFT JOIN FETCH co.orderProducts op LEFT JOIN FETCH op.product LEFT JOIN FETCH co.client WHERE co.clientOrderId = :id")
    Optional<ClientOrder> findByIdWithOrderProducts(@Param("id") Long id);

    @Query("SELECT co FROM ClientOrder co LEFT JOIN FETCH co.orderProducts op LEFT JOIN FETCH op.product LEFT JOIN FETCH co.client")
    Page<ClientOrder> findAllWithOrderProducts(Pageable pageable);

    @Query("SELECT co FROM ClientOrder co LEFT JOIN FETCH co.orderProducts op LEFT JOIN FETCH op.product LEFT JOIN FETCH co.client WHERE co.status = :status")
    Page<ClientOrder> findByStatusWithOrderProducts(@Param("status") ClientOrderStatus status, Pageable pageable);

}