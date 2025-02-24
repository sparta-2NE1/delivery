package com.sparta.delivery.domain.store.service;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
import com.sparta.delivery.config.global.exception.custom.StoreNotFoundException;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.dto.StoreRegionResDto;
import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.dto.StoreResDto;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.enums.UserRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResDto storeCreate(StoreReqDto storereqdto, PrincipalDetails userDetails) {//가게 저장
        Stores store = reqDtoToEntity(storereqdto);
        return entityToResDto(storeRepository.save(store));
    }

    public Page<StoreRegionResDto> getStoreList(Pageable pageable) { //가게 리스트 조회
        Page<Stores> storeList = storeRepository.findAllByDeletedAtIsNull(pageable);
        if (storeList.isEmpty()) {
            throw new StoreNotFoundException("가게가 한개도 등록되어있지 않습니다.");
        }

        return storeList.map(StoreRegionResDto::new);
    }

    public StoreRegionResDto getStoreOne(UUID id) {//가게 단일 조회
        return new StoreRegionResDto(storeRepository.findByStoreIdAndDeletedAtIsNull(id).orElseThrow(() -> new StoreNotFoundException("해당 가게가 존재하지 않습니다")));
    }

    public Stores updateStoreReview(UUID id, int star, int cnt) { //별점, 개수여부(+1 or -1)
        Stores store = storeRepository.findById(id).orElseThrow(() -> new StoreNotFoundException("가게를 등록할 수 없습니다."));
        store.setStarSum(store.getStarSum() + star);
        store.setReviewSum(store.getReviewSum() + cnt);
        return storeRepository.save(store);//
    }

    public List<StoreResDto> searchStore(String keyword, Pageable pageable, String categorys) {//가게 검색
        Category category = Category.valueOf(categorys);
        List<Stores> storeList = (keyword == null || keyword.trim().isEmpty()) ?
                storeRepository.findByCategoryAndDeletedAtIsNull(category) : storeRepository.findByNameContainingAndCategoryAndDeletedAtIsNull(keyword, category);
        List<Integer> Size_List = List.of(10, 20, 30);
        if (!Size_List.contains((pageable.getPageSize()))) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), storeList.size());

        if (storeList.isEmpty() || start >= storeList.size()) {
            throw new StoreNotFoundException("가게 검색 결과가 존재하지 않습니다.");
        }
        return storeList.subList(start, end)
                .stream()
                .map(StoreResDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoreResDto updateStore(StoreReqDto storereqdto, UUID id, PrincipalDetails userDetails) { //가게 업데이트
        checkoutIfOwner(userDetails);
        Stores store = storeRepository.findByStoreIdAndDeletedAtIsNull(id).orElseThrow(() -> new StoreNotFoundException("존재하지 않는 가게입니다."));

        store.setAddress(storereqdto.getAddress());
        store.setCategory(storereqdto.getCategory());
        store.setName(storereqdto.getName());

        return entityToResDto(store);
    }

    @Transactional
    public void deleteStore(UUID id, PrincipalDetails userDetails) {//가게 삭제
        checkoutIfOwner(userDetails);
        Stores store = storeRepository.findByStoreIdAndDeletedAtIsNull(id).orElseThrow(() -> new StoreNotFoundException("존재하지 않는 가게입니다."));
        store.setDeletedBy(userDetails.getUsername());
        store.setDeletedAt(LocalDateTime.now());
        if (store.getRegionList() != null) {
            for (Region region : store.getRegionList()) {
                region.setDeletedBy(userDetails.getUsername());
                region.setDeletedAt(LocalDateTime.now());
            }
        }
    }

    public StoreResDto entityToResDto(Stores stores) {

        return StoreResDto.builder()
                .name(stores.getName())
                .address(stores.getAddress())
                .status(stores.isStatus())
                //.regionList(stores.getRegionList())
                .category(stores.getCategory())
                .build();
    }


    public Stores reqDtoToEntity(StoreReqDto storeReqDto) {
        return Stores.builder()
                .name(storeReqDto.getName())
                .address(storeReqDto.getAddress())
                .category(storeReqDto.getCategory())
                .status(true)
                .build();
    }


    void checkoutIfOwner(PrincipalDetails userDetails) {
        if (userDetails.getRole() != UserRoles.ROLE_OWNER) {
            throw new ForbiddenException("(가계주인)허가된 사용자가 아닙니다.");
        }
    }

    void checkoutIfManager(PrincipalDetails userDetails) {
        if (userDetails.getRole() != UserRoles.ROLE_MANAGER) {
            throw new ForbiddenException("(관리자)허가된 사용자가 아닙니다.");
        }
    }

    void checkoutIfMaster(PrincipalDetails userDetails) {
        if (userDetails.getRole() != UserRoles.ROLE_MASTER) {
            throw new ForbiddenException("(최고관리자)허가된 사용자가 아닙니다.");
        }
    }

}
