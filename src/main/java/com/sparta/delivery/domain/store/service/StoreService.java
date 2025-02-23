package com.sparta.delivery.domain.store.service;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.StoreNotFoundException;
import com.sparta.delivery.config.global.exception.custom.UnauthorizedException;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.dto.StoreResDto;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.enums.UserRoles;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreResDto storeCreate(StoreReqDto storereqdto, PrincipalDetails userDetails) {//가게 저장
        checkoutIfMaster(userDetails);
        Stores store = reqDtoToEntity(storereqdto);
        return entityToResDto(storeRepository.save(store));
    }

    public Page<StoreResDto> getStoreList(Pageable pageable) { //가게 리스트 조회
        Page<Stores> storeList = storeRepository.findAllByDeletedAtIsNull(pageable);
        if (storeList.isEmpty()) {
            throw new StoreNotFoundException("가게가 한개도 등록되어있지 않습니다.");
        }

        return storeList.map(StoreResDto::new);
    }

    public StoreResDto getStoreOne(UUID id) {//가게 단일 조회
        return entityToResDto(storeRepository.findByStoreIdAndDeletedAtIsNull(id).orElseThrow(() -> new StoreNotFoundException("가계를 등록할 수 없습니다")));
    }

    public Stores updateStoreReview(UUID id, int star, int cnt) { //별점, 개수여부(+1 or -1)
        Stores store = storeRepository.findById(id).orElseThrow(() -> new StoreNotFoundException("가게를 등록할 수 없습니다."));
        store.setStarSum(store.getStarSum() + star);
        store.setReviewSum(store.getReviewSum() + cnt);
        return storeRepository.save(store);//
    }

    public List<StoreResDto> searchStore(String keyword, Pageable pageable, Category category) {//가게 검색
        List<Stores> storeList = (keyword == null || keyword.trim().isEmpty()) ?
                storeRepository.findByCategory(category) : storeRepository.findByNameContainingAndCategoryAndDeletedAtIsNull(keyword, category);//deletedAt필터추가필요! 다른것도다점검

        List<Integer> Size_List = List.of(10, 20, 30);
        if (!Size_List.contains((pageable.getPageSize()))) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }

        if (storeList.isEmpty()) {
            throw new StoreNotFoundException("가게가 한개도 등록되어있지 않습니다.");
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), storeList.size());

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
    public void deleteStore(UUID id, String username) {//가게 삭제
        Stores store = storeRepository.findByStoreIdAndDeletedAtIsNull(id).orElseThrow(() -> new StoreNotFoundException("존재하지 않는 가게입니다."));
        store.setDeletedBy(username);
        store.setDeletedAt(LocalDateTime.now());
        if (store.getRegionList() != null) {//가게삭제시 지역정보들도 같이처리
            for (Region region : store.getRegionList()) {
                region.setDeletedBy(username);
                region.setDeletedAt(LocalDateTime.now());
            }
        }
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
                .status(true)
                .build();
    }


    void checkoutIfOwner(PrincipalDetails userDetails) {
        if (userDetails.getRole() != UserRoles.ROLE_OWNER) {
            throw new UnauthorizedException("(가계주인)허가된 사용자가 아닙니다.");
        }
    }

    void checkoutIfMaster(PrincipalDetails userDetails) {
        if (userDetails.getRole() != UserRoles.ROLE_MASTER) {
            throw new UnauthorizedException("(관리자)허가된 사용자가 아닙니다.");
        }
    }

}
