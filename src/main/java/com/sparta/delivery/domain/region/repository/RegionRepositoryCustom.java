package com.sparta.delivery.domain.region.repository;

public interface RegionRepositoryCustom {

    List<Region> findByLocalityContainingAndDeletedAtIsNull(String locality, String sortBy, String order);

}
