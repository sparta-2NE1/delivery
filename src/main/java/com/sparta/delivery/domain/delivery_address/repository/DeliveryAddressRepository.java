package com.sparta.delivery.domain.delivery_address.repository;

import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress , UUID> {
}
