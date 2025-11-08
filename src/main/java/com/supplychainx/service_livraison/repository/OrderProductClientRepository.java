package com.supplychainx.service_livraison.repository;

import com.supplychainx.service_livraison.model.OrderProductClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderProductClientRepository extends JpaRepository<OrderProductClient, Long> {
}
