package com.sparta.delivery.domain.store.repository;

import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

import javax.swing.plaf.synth.Region;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Stores, UUID>, StoreRepositoryCustom {


    List<Stores> findByCategory(Category category);

    Optional<Stores> findByStoreIdAndDeletedAtIsNull(UUID id);//deletedAt이 null이아닌 가게 단건검색

    Page<Stores> findAllByDeletedAtIsNull(Pageable pageable);//deletedAt이 null이 아닌 가게조회

    Page<Stores> findAll(Pageable pageable);

}
