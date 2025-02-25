package com.sparta.delivery.domain.region.repository;

import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.entity.Stores;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID>, RegionRepositoryCustom {

    boolean existsByLocalityAndDeletedAtIsNull(String locality);

    boolean existsByLocalityAndStores_StoreIdAndDeletedAtIsNull(String locality, UUID storeId);

    List<Region> findByLocalityContainingAndDeletedAtIsNull(String locality);

    Optional<Region> findByRegionIdAndDeletedAtIsNull(UUID id);// 가게 단건검색

    Page<Region> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Region> findAllByStores_StoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);


}
