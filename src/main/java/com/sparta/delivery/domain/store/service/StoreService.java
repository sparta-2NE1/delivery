package com.sparta.delivery.domain.store.service;


import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.dto.StoreResDto;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    @Autowired
    private final StoreRepository storeRepository;

    public StoreResDto storeCreate(StoreReqDto storereqdto) {//가게 저장
        Stores store = reqDtoToEntity(storereqdto); //entity->DTO 변환
        return entityToResDto(storeRepository.save(store));
    }

    public StoreResDto entityToResDto(Stores stores) {

        return StoreResDto.builder()
                .name(stores.getName())
                .address(stores.getAddress())
                .status(stores.isStatus())
                .regionList(stores.getRegionList())
                .category(stores.getCategory())
                .build();
    }


    public Stores reqDtoToEntity(StoreReqDto storeReqDto) {
        return Stores.builder()
                .name(storeReqDto.getName())
                .address(storeReqDto.getAddress())
                .category(storeReqDto.getCategory())
                .status(true) // 기본값 true
                .build();
    }

}
