package com.supplychainx.service_livraison.repository;

import com.supplychainx.service_livraison.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address , Long> {
    Address findByStreetIgnoreCase(String street);
}
