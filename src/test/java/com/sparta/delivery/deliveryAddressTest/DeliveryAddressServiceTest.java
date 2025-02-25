//package com.sparta.delivery.deliveryAddressTest;
//
//import com.querydsl.core.BooleanBuilder;
//import com.sparta.delivery.config.auth.PrincipalDetails;
//import com.sparta.delivery.config.global.exception.custom.DeliveryAddressNotFoundException;
//import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
//import com.sparta.delivery.domain.delivery_address.dto.AddressReqDto;
//import com.sparta.delivery.domain.delivery_address.dto.AddressResDto;
//import com.sparta.delivery.domain.delivery_address.dto.AddressSearchDto;
//import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
//import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
//import com.sparta.delivery.domain.delivery_address.service.DeliveryAddressService;
//import com.sparta.delivery.domain.user.entity.User;
//import com.sparta.delivery.domain.user.enums.UserRoles;
//import com.sparta.delivery.domain.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//public class DeliveryAddressServiceTest {
//
//    @InjectMocks
//    private DeliveryAddressService deliveryAddressService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private DeliveryAddressRepository addressRepository;
//
//    private User testUser;
//    private UUID userId;
//
//    private DeliveryAddress testDeliveryAddress;
//    private UUID addressId;
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/delivery");
//        registry.add("spring.datasource.username", () -> "twenty1");
//        registry.add("spring.datasource.password", () -> "twenty1");
//        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
//        registry.add("spring.jwt.secret", () -> "test_secret_key");
//        registry.add("ai.apikey", () -> "test_api_key");
//    }
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // 테스트용 사용자 생성
//        userId = UUID.randomUUID();
//        testUser = User.builder()
//                .userId(userId)
//                .email("test@example.com")
//                .password("encodedPassword")
//                .username("testuser")
//                .nickname("testnick")
//                .role(UserRoles.ROLE_CUSTOMER)
//                .deliveryAddresses(new ArrayList<>())
//                .build();
//
//        addressId = UUID.randomUUID();
//        testDeliveryAddress = DeliveryAddress.builder()
//                .deliveryAddressId(addressId)
//                .deliveryAddress("testHome")
//                .deliveryAddressInfo("Gwanghwamun")
//                .detailAddress("101")
//                .user(testUser)
//                .build();
//
//    }
//
//
//    @Test
//    @DisplayName("배송지 등록 성공")
//    void testAddAddressSuccess(){
//        // Given
//        AddressReqDto addressReqDto = new AddressReqDto("home","Gwanghwamun","101");
//
//        // PrincipalDetails mock 객체 생성
//        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
//        when(principalDetails.getUsername()).thenReturn("testuser");
//
//        when(userRepository.findByUsernameAndDeletedAtIsNull(principalDetails.getUsername())).thenReturn(Optional.of(testUser));
//        when(addressRepository.existsByUserAndDeliveryAddressAndDeletedAtIsNull(testUser, addressReqDto.getDeliveryAddress())).thenReturn(false);
//        when(addressRepository.countByUserAndDeletedAtIsNull(testUser)).thenReturn(0L);
//
//        // When
//        AddressResDto result = deliveryAddressService.addAddress(addressReqDto,principalDetails);
//
//        // Then
//        assertNotNull(result);
//        assertEquals("home", result.getDeliveryAddress());
//        assertEquals("Gwanghwamun", result.getDeliveryAddressInfo());
//    }
//
//    @Test
//    @DisplayName("배송지 등록 실패 - 유저의 중복된 배송지명 이 존재할 때")
//    void testAddAddressFailsWhenDuplicateAddressExists(){
//        // Given
//        AddressReqDto addressReqDto = new AddressReqDto("home","Gwanghwamun","101");
//
//        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
//        when(principalDetails.getUsername()).thenReturn("testuser");
//
//        when(userRepository.findByUsernameAndDeletedAtIsNull(principalDetails.getUsername())).thenReturn(Optional.of(testUser));
//        when(addressRepository.existsByUserAndDeliveryAddressAndDeletedAtIsNull(testUser, addressReqDto.getDeliveryAddress()))
//                .thenReturn(true);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                ()-> deliveryAddressService.addAddress(addressReqDto,principalDetails));
//        assertEquals("해당 유저의 동일한 배송지가 이미 존재합니다. :home", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("배송지 등록 실패 - 유저가 가진 배송지주소가 3개 이상일 때")
//    void testAddAddressFailsWhenUserHasThreeOrMoreAddresses(){
//        // Given
//        AddressReqDto addressReqDto = new AddressReqDto("home","Gwanghwamun","101");
//
//        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
//        when(principalDetails.getUsername()).thenReturn("testuser");
//
//        when(userRepository.findByUsernameAndDeletedAtIsNull(principalDetails.getUsername())).thenReturn(Optional.of(testUser));
//        when(addressRepository.existsByUserAndDeliveryAddressAndDeletedAtIsNull(testUser, addressReqDto.getDeliveryAddress()))
//                .thenReturn(false);
//        when(addressRepository.countByUserAndDeletedAtIsNull(testUser)).thenReturn(4L);
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                ()-> deliveryAddressService.addAddress(addressReqDto,principalDetails));
//        assertEquals("배송지는 최대 3개 까지만 추가할 수 있습니다.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("배송지 단일 조회 성공")
//    void testGetAddressSuccess(){
//
//        // Given
//        when(addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(addressId)).thenReturn(Optional.of(testDeliveryAddress));
//
//        // When
//        AddressResDto result = deliveryAddressService.getDeliveryAddressById(addressId);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(testDeliveryAddress.getDeliveryAddress() , result.getDeliveryAddress());
//
//    }
//
//    @Test
//    @DisplayName("배송지 단일 조회 실패 - 존재하지않는 ID")
//    void testGetAddressFail(){
//
//        // Given
//        when(addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(addressId)).thenReturn(Optional.empty());
//
//        // When Then
//        DeliveryAddressNotFoundException exception = assertThrows(DeliveryAddressNotFoundException.class,
//                ()-> deliveryAddressService.getDeliveryAddressById(addressId));
//        assertEquals("DeliveryAddress Not Found By Id : "+addressId, exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("모든 배송지 조회 성공")
//    void testGetDeliveryAddressesSuccess(){
//
//        // Given
//        AddressSearchDto addressSearchDto = new AddressSearchDto();
//        addressSearchDto.setPage(0);
//        addressSearchDto.setSize(10);
//        addressSearchDto.setSortBy("createdAt");
//        addressSearchDto.setOrder("asc");
//
//
//        // 테스트 배송지 객체 생성
//        DeliveryAddress testDeliveryAddress = DeliveryAddress.builder()
//                .deliveryAddressId(UUID.randomUUID())
//                .deliveryAddress("testHome")
//                .deliveryAddressInfo("Gwanghwamun")
//                .detailAddress("101")
//                .user(testUser)
//                .build();
//
//        // Given
//        Page<DeliveryAddress> deliveryAddressPage = new PageImpl<>(List.of(testDeliveryAddress));
//        when(addressRepository.findAll(any(BooleanBuilder.class), any(Pageable.class))).thenReturn(deliveryAddressPage);
//
//        // When
//        Page<AddressResDto> result = deliveryAddressService.getDeliveryAddresses(addressSearchDto);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals("Gwanghwamun", result.getContent().get(0).getDeliveryAddressInfo());
//    }
//
//    @Test
//    @DisplayName("모든 배송지 조회 실패 - 결과 없음")
//    void testGetDeliveryAddressesFail(){
//        // Given
//        AddressSearchDto addressSearchDto = new AddressSearchDto();
//        addressSearchDto.setPage(0);
//        addressSearchDto.setSize(10);
//        addressSearchDto.setSortBy("createdAt");
//        addressSearchDto.setOrder("asc");
//
//        Page<DeliveryAddress> deliveryAddressPage = Page.empty();
//        when(addressRepository.findAll(any(BooleanBuilder.class), any(Pageable.class))).thenReturn(deliveryAddressPage);
//
//        // When & Then
//        DeliveryAddressNotFoundException exception = assertThrows(DeliveryAddressNotFoundException.class,
//                ()-> deliveryAddressService.getDeliveryAddresses(addressSearchDto));
//        assertEquals("deliveryAddress Not Found", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("배송지 정보 수정 성공 테스트")
//    void testUpdateDeliveryAddressesSuccess(){
//        // Given
//        AddressReqDto addressReqDto = new AddressReqDto("updateHome","new info","202");
//
//        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
//        when(principalDetails.getUsername()).thenReturn("testuser");
//        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);
//
//        // 기존 배송지 객체
//        when(addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(addressId))
//                .thenReturn(Optional.of(testDeliveryAddress));
//
//        DeliveryAddress updateDeliveryAddress = testDeliveryAddress.toBuilder()
//                .deliveryAddress("updateHome")
//                .deliveryAddressInfo("new info")
//                .detailAddress("202")
//                .build();
//
//
//        when(addressRepository.save(any(DeliveryAddress.class))).thenReturn(updateDeliveryAddress);
//
//        // When
//        AddressResDto result = deliveryAddressService.updateDeliveryAddresses(addressId, addressReqDto, principalDetails);
//
//        // Then
//        assertNotNull(result);
//        assertEquals("updateHome", result.getDeliveryAddress());
//        assertEquals("new info", result.getDeliveryAddressInfo());
//        assertEquals("202", result.getDetailAddress());
//
//    }
//
//    @Test
//    @DisplayName("배송지 정보 수정 실패 - 권한 없음")
//    void testUpdateDeliveryAddressesFail(){
//        // Given
//        AddressReqDto addressReqDto = new AddressReqDto("updateHome","new info","202");
//
//        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
//        when(principalDetails.getUsername()).thenReturn("otherUser");
//        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);
//
//        when(addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(addressId))
//                .thenReturn(Optional.of(testDeliveryAddress));
//
//        // When & Then
//        ForbiddenException exception = assertThrows(ForbiddenException.class,
//                () -> deliveryAddressService.updateDeliveryAddresses(addressId, addressReqDto, principalDetails));
//
//        assertEquals("Access denied.", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("배송지 삭제 성공")
//    void testDeleteDeliveryAddressesSuccess(){
//
//        // Given
//        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
//        when(principalDetails.getUsername()).thenReturn(testUser.getUsername());
//        when(addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(addressId)).thenReturn(Optional.of(testDeliveryAddress));
//
//        // When & Then
//        assertDoesNotThrow(() -> deliveryAddressService.deleteDeliveryAddresses(addressId,principalDetails));
//        verify(addressRepository, times(1)).save(any(DeliveryAddress.class));
//
//    }
//
//    @Test
//    @DisplayName("배송지 삭제 실패")
//    void testDeleteDeliveryAddressesFail(){
//
//        // Given
//        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
//        when(principalDetails.getUsername()).thenReturn("otherUser");
//        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);
//        when(addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(addressId)).thenReturn(Optional.of(testDeliveryAddress));
//
//        // When & Then
//        ForbiddenException exception = assertThrows(ForbiddenException.class,
//                () -> deliveryAddressService.deleteDeliveryAddresses(addressId,principalDetails));
//        assertEquals("Access denied.", exception.getMessage());
//
//    }
//
//}
