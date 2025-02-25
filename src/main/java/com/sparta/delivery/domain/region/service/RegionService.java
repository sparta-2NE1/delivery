package com.sparta.delivery.domain.region.service;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
import com.sparta.delivery.config.global.exception.custom.RegionNotFoundException;
import com.sparta.delivery.config.global.exception.custom.StoreNotFoundException;
import com.sparta.delivery.config.global.exception.custom.UnauthorizedException;
import com.sparta.delivery.domain.region.dto.RegionReqDto;
import com.sparta.delivery.domain.region.dto.RegionResDto;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.enums.UserRoles;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final StoreRepository storeRepository;

    @Transactional
    public RegionResDto regionCreate(RegionReqDto regionReqDto, PrincipalDetails userDetails) { //운영 지역 생성
        checkoutIfOwner(userDetails);
        Region region = reqDtoToEntity(regionReqDto);
        // 지역 중복 체크
        if (regionRepository.existsByLocalityAndStores_StoreIdAndDeletedAtIsNull(regionReqDto.getLocality(),regionReqDto.getStoreId())) {
            throw new IllegalArgumentException("이미 존재하는 지역입니다.");
        }
        if (regionReqDto.getStoreId() == null) {
            throw new StoreNotFoundException("유효하지 않은 가게ID 입니다.");
        }
        Stores store = storeRepository.findByStoreIdAndDeletedAtIsNull(regionReqDto.getStoreId()).orElseThrow(()
                -> new StoreNotFoundException("존재하지 않는 가게입니다"));
        region.setStores(store);
        return entityToResDto(regionRepository.save(region));
    }

    public Page<RegionResDto> getRegionList(Pageable pageable, UUID id) { //특정 가게 운영 지역 리스트 조회
        if (id == null) {
            throw new StoreNotFoundException("가게ID 정보가 없습니다.");
        }
        Page<Region> regionList = regionRepository.findAllByStores_StoreIdAndDeletedAtIsNull(id, pageable);
        if (regionList.isEmpty()) {
            throw new RegionNotFoundException("지역이 한개도 등록되어있지 않습니다.");
        }
        return regionList.map(RegionResDto::new);

    }

    public Page<RegionResDto> getAllRegionList(Pageable pageable) { //전체 운영 지역 리스트 조회
        Page<Region> regionList = regionRepository.findAllByDeletedAtIsNull(pageable);
        if (regionList.isEmpty()) {
            throw new RegionNotFoundException("지역이 한개도 등록되어있지 않습니다.");
        }
        return regionList.map(RegionResDto::new);

    }

    public List<RegionResDto> searchRegion(String keyword, Pageable pageable, String sortBy, String order) { //운영 지역 검색(동 기준으로만검색됨)
        List<Region> regionList = regionRepository.findByLocalityContainingAndDeletedAtIsNull(keyword, sortBy, order);
        List<Integer> Size_List = List.of(10, 20, 30);
        if (regionList.isEmpty()) {
            throw new RegionNotFoundException("지역이 한개도 등록되어있지 않습니다.");
        }
        if (!Size_List.contains((pageable.getPageSize()))) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), regionList.size());

        if (regionList.isEmpty() || start >= regionList.size()) {
            throw new RegionNotFoundException("지역 검색 결과가 존재하지 않습니다.");
        }
        return regionList.subList(start, end)
                .stream()
                .map(RegionResDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public RegionResDto updateRegion(RegionReqDto regionReqDto, UUID id, PrincipalDetails userDetails) { //운영 지역 업데이트
        checkoutIfOwner(userDetails);
        Region region = regionRepository.findByRegionIdAndDeletedAtIsNull(id).orElseThrow(() -> new RegionNotFoundException("존재하지 않는 지역입니다."));

        region.setProvince("도시");
        region.setCity("서울");
        region.setLocality(regionReqDto.getLocality());
        return entityToResDto(region);
    }

    @Transactional
    public void deleteRegion(UUID id, PrincipalDetails userDetails) {//운영 지역 삭제
        checkoutIfOwner(userDetails);
        Region region = regionRepository.findByRegionIdAndDeletedAtIsNull(id).orElseThrow(() -> new RegionNotFoundException("존재하지 않는 지역입니다."));
        region.setDeletedBy(userDetails.getUsername());
        region.setDeletedAt(LocalDateTime.now());

    }

    public Region reqDtoToEntity(RegionReqDto regionReqDto) {

        return Region.builder().province(regionReqDto.getProvince()).city(regionReqDto.getCity()).
                locality(regionReqDto.getLocality()).build();

    }

    public RegionResDto entityToResDto(Region region) {

        return RegionResDto.builder().province("수도").city("서울").
                locality(region.getLocality()).build();

    }

    void checkoutIfOwner(PrincipalDetails userDetails) {
        if (userDetails.getRole() != UserRoles.ROLE_OWNER) {
            throw new ForbiddenException("(가계주인)허가된 사용자가 아닙니다.");
        }
    }

}
