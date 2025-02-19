package com.sparta.delivery.domain.region.service;

import com.sparta.delivery.domain.region.dto.RegionReqDto;
import com.sparta.delivery.domain.region.dto.RegionResDto;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@AllArgsConstructor
public class RegionService {

    @Autowired
    private final RegionRepository regionRepository;

    public RegionResDto regionCreate(RegionReqDto regionReqDto){ //운영 지역 생성

        Region region = reqDtoToEntity(regionReqDto);

        return entityToResDto(regionRepository.save(region)) ;
    }
    public RegionResDto getRegionOne(UUID id){//가게 단일 조회
//        UUID uuid = convertHexToUUID(id); //16진수->uuid로 변환한다! UUID형식 8-4-4-4-12
        return entityToResDto(regionRepository.findById(id).orElseThrow(()-> new EntityNotFoundException()));
    }

    public List<RegionResDto> getSRegionList(){ //운영 지역 리스트 조회
        List<Region> regionList = regionRepository.findAll();
        return regionList.stream().map(RegionResDto::new).collect(Collectors.toList()); //

    }

    public Region reqDtoToEntity(RegionReqDto regionReqDto){

        return Region.builder().province(regionReqDto.getProvince()).city(regionReqDto.getCity()).
                locality(regionReqDto.getLocality()).build();

    }
    public RegionResDto entityToResDto(Region region){

        return RegionResDto.builder().province(region.getProvince()).city(region.getCity()).
                locality(region.getLocality()).build();

    }
}
