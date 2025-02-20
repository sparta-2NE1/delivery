package com.sparta.delivery.domain.region.repository;

import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.entity.Stores;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {


    List<Region> findByProvince(String province);  // 등록한 도(province) 정보 조회


    List<Region> findByCity(String city);  // 등록한 시(city) 정보 조회


    List<Region> findByLocality(String locality);  // 등록한 동(locality) 조회

    List<Region> findByLocalityContainingAndDeletedAtIsNull(String locality);

    Optional<Region> findByRegionIdAndDeletedAtIsNull(UUID id);//deletedAt이 null이아닌 가게 단건검색

    Page<Region> findAllByDeletedAtIsNull(Pageable pageable);//deletedAt이 null이 아닌 가게조회


}
