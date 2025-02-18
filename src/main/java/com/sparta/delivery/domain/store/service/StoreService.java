package com.sparta.delivery.domain.store.service;


import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.dto.StoreResDto;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    @Autowired
    private final StoreRepository storeRepository;

    public StoreResDto storeCreate(StoreReqDto storereqdto) {//가게 저장
        Stores store = reqDtoToEntity(storereqdto); //entity->DTO 변환
        return entityToResDto(storeRepository.save(store));
    }

    public List<StoreResDto> getStoreList(){ //가게 리스트 조회
        List<Stores> storeList = storeRepository.findAll();

        if(storeList.isEmpty()){throw new NoSuchElementException("매장이 한개도 등록되어있지 않습니다.");}

        return storeList.stream().map(StoreResDto::new).collect(Collectors.toList());
    }

    public StoreResDto getStoreOne(UUID id){//가게 단일 조회
        return entityToResDto( storeRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("가계를찾을수없어요")));
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
