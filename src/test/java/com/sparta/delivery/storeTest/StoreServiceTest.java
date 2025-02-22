package com.sparta.delivery.storeTest;


import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.StoreNotFoundException;
import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.dto.StoreResDto;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.store.service.StoreService;

import com.sparta.delivery.domain.user.enums.UserRoles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StoreServiceTest {
    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    private Stores testStore;
    private UUID storeId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        StoreReqDto storeReqDto = new StoreReqDto("본죽", Category.한식, "종로동");
        storeId = UUID.randomUUID();
        testStore = Stores.builder().storeId(storeId).name("본죽").address("종로동").category(Category.한식).build();
    }

    @Test
    @DisplayName("가게등록 성공 테스트")
    void testRegisterSuccess() {
        // Given
        // PrincipalDetails mock 객체 생성
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_MASTER);//권한설정
        StoreReqDto storeReqDto = new StoreReqDto("본죽", Category.한식, "종로동");
        when(storeRepository.save(any(Stores.class))).thenReturn(testStore);

        // When - 가게를 저장했을때
        StoreResDto result = storeService.storeCreate(storeReqDto, principalDetails);

        // Then - 더미데이터와 일치하는지 검사
        assertNotNull(result);
        assertEquals("본죽", result.getName());
        assertEquals("종로동", result.getAddress());
        assertEquals(Category.한식, result.getCategory());
    }


    @Test
    @DisplayName("가게 리스트 조회 성공 테스트")
    void testGetListSuccess() {
        // Given
        Stores testStore = Stores.builder().name("본죽").address("종로동").category(Category.한식).build();
        Stores testStore2 = Stores.builder().name("쌀죽").address("본동").category(Category.한식).build();

        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());
        Page<Stores> storeList = new PageImpl<>(List.of(testStore, testStore2), pageable, 2);
        when(storeRepository.findAllByDeletedAtIsNull(any(Pageable.class))).thenReturn(storeList);

        // When - 가게를 저장했을때
        Page<StoreResDto> result = storeService.getStoreList(pageable);

        // Then - 더미데이터와 일치하는지 검사
        assertNotNull(result);
        assertEquals(result.getTotalElements(), 2);

        assertEquals("본죽", result.getContent().get(0).getName());
        assertEquals("종로동", result.getContent().get(0).getAddress());
        assertEquals(Category.한식, result.getContent().get(0).getCategory());

        //두번째 가게 검증
        assertEquals("쌀죽", result.getContent().get(1).getName());
        assertEquals("본동", result.getContent().get(1).getAddress());
        assertEquals(Category.한식, result.getContent().get(1).getCategory());
    }

    @Test
    @DisplayName("가게 리스트 조회 실패 테스트")
    void testGetListFail() {
        // Given
        Page<Stores> storeList = new PageImpl<>(List.of(), PageRequest.of(0, 2), 2);
        when(storeRepository.findAllByDeletedAtIsNull(any(Pageable.class))).thenReturn(storeList);
        //When && Then - 예외발생 여부 테스트
        assertThrows(StoreNotFoundException.class, () -> storeService.getStoreList(PageRequest.of(0, 2)));
    }

    @Test
    @DisplayName("가게 단일 조회 테스트")
    void testGetOneSuccess() {
        // Given
        Stores testStore = Stores.builder().name("본죽").address("종로동").category(Category.한식).storeId(storeId).build();
        when(storeRepository.findByStoreIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(Optional.of(testStore));

        // When - 가게를 저장했을때
        StoreResDto result = storeService.getStoreOne(storeId);

        // Then - 더미데이터와 일치하는지 검사
        assertNotNull(result);

        assertEquals("본죽", result.getName());
        assertEquals("종로동", result.getAddress());
        assertEquals(Category.한식, result.getCategory());

    }

    @Test
    @DisplayName("가게 단일 조회 실패 테스트")
    void testGetOnetFail() {
        // Given
        Stores testStore = Stores.builder().address("종로동").category(Category.한식).storeId(storeId).build();
        when(storeRepository.findByStoreIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(Optional.empty());
        //When && Then - 예외발생 여부 테스트
        assertThrows(StoreNotFoundException.class, () -> storeService.getStoreOne(storeId));
    }

    @Test
    @DisplayName("가게 수정 테스트")
    void testUpdateSuccess() {
        // Given
        // PrincipalDetails mock 객체 생성
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);
        Stores testStore = Stores.builder().name("본죽").address("종로동").category(Category.한식).storeId(storeId).build();
        StoreReqDto storeReqDto = StoreReqDto.builder().name("흰죽").address("청학동").category(Category.분식).build();
        when(storeRepository.findByStoreIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(Optional.of(testStore));

        // When - 가게를 저장했을때
        StoreResDto result = storeService.updateStore(storeReqDto, storeId, principalDetails);

        // Then - 더미데이터와 일치하는지 검사
        assertNotNull(result);

        assertEquals("흰죽", result.getName());
        assertEquals("청학동", result.getAddress());
        assertEquals(Category.분식, result.getCategory());

    }

    @Test
    @DisplayName("가게 수정 실패 테스트")
    void testUpdateFail() {
        // Given
        // PrincipalDetails mock 객체 생성
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);
        StoreReqDto storeReqDto = StoreReqDto.builder().name("흰죽").address("청학동").category(Category.분식).build();
        when(storeRepository.findByStoreIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(Optional.empty());

        //When && Then - 예외발생 여부 테스트
        assertThrows(StoreNotFoundException.class, () -> storeService.updateStore(storeReqDto, storeId, principalDetails));

    }

    @Test
    @DisplayName("가게 삭제 테스트")
    void testDeleteSuccess() {
        // Given
        String username = "Tom";//실제로그인하지 않으므로 임의의 아이디값
        Stores testStore = Stores.builder().name("본죽").address("종로동").category(Category.한식).storeId(storeId).build();
        when(storeRepository.findByStoreIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(Optional.of(testStore));

        // When - 가게를 저장했을때
        storeService.deleteStore(storeId, username);//여기서 result는 set까지완료한 객체
        Stores result = testStore;

        // Then - 더미데이터와 일치하는지 검사
        assertNotNull(result);
        assertNotNull(result.getDeletedBy()); //deletedAt null이 아니면 통과
        assertNotNull(result.getDeletedAt());


    }

    @Test
    @DisplayName("가게 삭제 실패 테스트")
    void testDeleteFail() {
        // Given
        String username = "Tom";//실제로그인하지 않으므로 임의의 아이디값
        when(storeRepository.findByStoreIdAndDeletedAtIsNull(any(UUID.class))).thenReturn(Optional.empty());

        //When && Then - 예외발생 여부 테스트
        assertThrows(StoreNotFoundException.class, () -> storeService.deleteStore(storeId, username));

    }

}
