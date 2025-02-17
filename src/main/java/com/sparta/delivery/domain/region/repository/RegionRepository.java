package com.sparta.delivery.domain.region.repository;

import com.sparta.delivery.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {


    List<Region> findByProvince(String province);  // 등록한 도(province) 정보 조회


    List<Region> findByCity(String city);  // 등록한 시(city) 정보 조회


    List<Region> findByLocality(String locality);  // 등록한 동(locality) 조회





}
