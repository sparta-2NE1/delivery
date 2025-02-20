package com.sparta.delivery.domain.region.service;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.region.dto.RegionReqDto;
import com.sparta.delivery.domain.region.dto.RegionResDto;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@AllArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionResDto regionCreate(RegionReqDto regionReqDto){ //운영 지역 생성

        Region region = reqDtoToEntity(regionReqDto);

        return entityToResDto(regionRepository.save(region)) ;
    }

    public RegionResDto getRegionOne(UUID id){//운영지역 단일 조회
        return entityToResDto(regionRepository.findByRegionIdAndDeletedAtIsNull(id).orElseThrow(()-> new EntityNotFoundException()));
    }

    public Page<RegionResDto> getsRegionList(Pageable pageable){ //운영 지역 리스트 조회
        Page<Region> regionList = regionRepository.findAllByDeletedAtIsNull(pageable);
        return regionList.map(RegionResDto::new);

    }

    public List<RegionResDto> searchRegion(String keyword,Pageable pageable){ //운영 지역 검색(동 기준으로만검색됨)

        List<Region> regionList = regionRepository.findByLocalityContainingAndDeletedAtIsNull(keyword);
        if(regionList.isEmpty()){throw new NoSuchElementException("매장이 한개도 등록되어있지 않습니다.");}

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), regionList.size());

        List<RegionResDto> dtoList = regionList.subList(start, end)
                .stream()
                .map(RegionResDto::new)
                .collect(Collectors.toList());

        return regionList.subList(start, end)
                .stream()
                .map(RegionResDto::new)
                .collect(Collectors.toList());

    }

    @Transactional
    public RegionResDto updateRegion(RegionReqDto regionReqDto, UUID id){ //운영 지역 업데이트
        Region region = regionRepository.findByRegionIdAndDeletedAtIsNull(id).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 지역입니다."));

        region.setProvince(regionReqDto.getProvince());
        region.setCity(regionReqDto.getCity()); region.setLocality(regionReqDto.getLocality());

        return entityToResDto(region);//
    }

    @Transactional
    public void deleteRegion(UUID id,String username){//운영 지역 삭제
        Region region=regionRepository.findByRegionIdAndDeletedAtIsNull(id).orElse(null);
        region.setDeletedBy(username); region.setDeletedAt(LocalDateTime.now());

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
