package com.sparta.delivery.domain.store.repository;

import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.plaf.synth.Region;
import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Stores,Long> {

    Optional<Stores> findByName(String name);

    List<Stores> findByAddressContaining(String address); //주소 일부만포함해도 검색

    List<Stores> findByStatusTrue(); //운영중인 가게 검색

    Optional<Stores> findByCategory(Category category);

    List<Stores> findByRegion(Region region);



}
