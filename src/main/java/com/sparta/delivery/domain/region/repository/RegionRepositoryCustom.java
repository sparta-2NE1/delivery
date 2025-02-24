package com.sparta.delivery.domain.region.repository;

import com.sparta.delivery.domain.region.entity.Region;

import java.util.List;

public interface RegionRepositoryCustom {

    List<Region> findByLocalityContainingAndDeletedAtIsNull(String locality, String sortBy, String order);

}
