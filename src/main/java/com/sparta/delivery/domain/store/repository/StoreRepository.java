package com.sparta.delivery.domain.store.repository;

import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.plaf.synth.Region;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Stores, UUID> {

    Optional<Stores> findByName(String name);

    List<Stores> findByAddressContaining(String address); //주소 일부만포함해도 검색

    List<Stores> findByStatusTrue(); //운영중인 가게 검색

    List<Stores> findByCategory(Category category);

    List<Stores> findByCityContaining(String city);//시로검색

    List<Stores> findByLocalityContaining(String locality);//동으로검색



}
