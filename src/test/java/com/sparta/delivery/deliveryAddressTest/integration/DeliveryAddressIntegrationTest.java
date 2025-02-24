package com.sparta.delivery.deliveryAddressTest.integration;


import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.DeliveryAddressNotFoundException;
import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
import com.sparta.delivery.domain.delivery_address.dto.AddressReqDto;
import com.sparta.delivery.domain.delivery_address.dto.AddressResDto;
import com.sparta.delivery.domain.delivery_address.dto.AddressSearchDto;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.delivery_address.service.DeliveryAddressService;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test") // application-test.yml을 기준으로 test 실행 환경을 구성합니다.
@SpringBootTest  // Spring 컨텍스트를 로드하여 통합 테스트 수행
@Transactional  // 각 테스트 후 롤백하여 데이터 정합성 유지
public class DeliveryAddressIntegrationTest {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @Autowired
    private DeliveryAddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    private DeliveryAddress testDeliveryAddress;
    private UUID addressId;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("encodedPassword")
                .username("testuser")
                .nickname("testnick")
                .role(UserRoles.ROLE_CUSTOMER)
                .deliveryAddresses(new ArrayList<>())
                .build();

        testUser = userRepository.save(testUser);

        testDeliveryAddress = DeliveryAddress.builder()
                .deliveryAddressId(UUID.randomUUID())
                .deliveryAddress("testHome")
                .deliveryAddressInfo("Gwanghwamun")
                .detailAddress("101")
                .user(testUser)
                .build();

        testDeliveryAddress = addressRepository.save(testDeliveryAddress);
        addressId = testDeliveryAddress.getDeliveryAddressId();
    }

    @Test
    @DisplayName("배송지 등록 성공")
    void testAddAddressSuccess() {

        // Given
        AddressReqDto addressReqDto = new AddressReqDto("home", "Gwanghwamun", "101");

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("testuser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        AddressResDto result = deliveryAddressService.addAddress(addressReqDto, principalDetails);

        // Then
        assertNotNull(result);
        assertEquals("home", result.getDeliveryAddress());
        assertEquals("Gwanghwamun", result.getDeliveryAddressInfo());
    }

    @Test
    @DisplayName("배송지 등록 삭제 - 유저의 중복된 배송지명 이 존재할 때")
    void testAddAddressFailsWhenDuplicateAddressExists(){

        // Given
        AddressReqDto addressReqDto = new AddressReqDto("testHome", "Gwanghwamun", "101");

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("testuser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> {
            deliveryAddressService.addAddress(addressReqDto,principalDetails);
        });

        // Then
        assertEquals("해당 유저의 동일한 배송지가 이미 존재합니다. :" + addressReqDto.getDeliveryAddress(), exception.getMessage());
    }

    @Test
    @DisplayName("배송지 등록 실패 - 유저가 가진 배송지주소가 3개 이상일 때")
    void testAddAddressFailsWhenUserHasThreeOrMoreAddresses(){

        // Given
        for (int i = 0; i < 2; i++) {
            AddressReqDto addressReqDto = new AddressReqDto("testHome"+ i + 1, "Gwanghwamun", "101");

            PrincipalDetails principalDetails = mock(PrincipalDetails.class);
            when(principalDetails.getUsername()).thenReturn("testuser");
            when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

            deliveryAddressService.addAddress(addressReqDto,principalDetails);
        }

        // When
        AddressReqDto addressReqDto = new AddressReqDto("testHome3" , "Gwanghwamun", "101");

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("testuser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()-> {
            deliveryAddressService.addAddress(addressReqDto,principalDetails);
        });

        assertEquals("배송지는 최대 3개 까지만 추가할 수 있습니다." , exception.getMessage());
    }

    @Test
    @DisplayName("배송지 단일 조회 성공")
    void testGetAddressSuccess() {

        AddressResDto result = deliveryAddressService.getDeliveryAddressById(addressId);

        assertNotNull(result);
        assertEquals(testDeliveryAddress.getDeliveryAddress(), result.getDeliveryAddress());
    }


    @Test
    @DisplayName("배송지 단일 조회 실패 - 존재하지 않는 ID")
    void testGetAddressFail() {

        // Given
        UUID invalidAddressId = UUID.randomUUID();

        // When
        DeliveryAddressNotFoundException exception = assertThrows(DeliveryAddressNotFoundException.class, () ->{
                    deliveryAddressService.getDeliveryAddressById(invalidAddressId);
        });

        // Then
        assertEquals("DeliveryAddress Not Found By Id : " + invalidAddressId, exception.getMessage());
    }

    @Test
    @DisplayName("모든 배송지 조회 성공")
    void testGetDeliveryAddressesSuccess(){

        // Given
        AddressSearchDto addressSearchDto = new AddressSearchDto();
        addressSearchDto.setPage(0);
        addressSearchDto.setSize(10);
        addressSearchDto.setSortBy("createdAt");
        addressSearchDto.setOrder("asc");

        for(int i = 0; i < 10; i++){
           DeliveryAddress testDeliveryAddress = DeliveryAddress.builder()
                    .deliveryAddressId(UUID.randomUUID())
                    .deliveryAddress("testHome" + i)
                    .deliveryAddressInfo("Gwanghwamun")
                    .detailAddress("101")
                    .user(testUser)
                    .build();

            addressRepository.save(testDeliveryAddress);
        }

        // When
        Page<AddressResDto> result = deliveryAddressService.getDeliveryAddresses(addressSearchDto);

        // Then
        assertNotNull(result);
        assertEquals(11 , result.getTotalElements());
        assertEquals("testHome0", result.getContent().get(1).getDeliveryAddress());
    }


    @Test
    @DisplayName("모든 배송지 조회 성공")
    void testGetDeliveryAddressesFail(){

        // Given
        AddressSearchDto addressSearchDto = new AddressSearchDto();
        addressSearchDto.setPage(0);
        addressSearchDto.setSize(10);
        addressSearchDto.setSortBy("createdAt");
        addressSearchDto.setOrder("asc");
        addressSearchDto.setDeliveryAddress("dummyHome");

        // When
        DeliveryAddressNotFoundException exception = assertThrows(DeliveryAddressNotFoundException.class, () ->{
            deliveryAddressService.getDeliveryAddresses(addressSearchDto);
        });

        // Then
        assertEquals("deliveryAddress Not Found" , exception.getMessage());
    }

    @Test
    @DisplayName("배송지 정보 수정 성공 테스트")
    void testUpdateDeliveryAddressesSuccess(){
        // Given
        AddressReqDto addressReqDto = new AddressReqDto("updateHome","new info","202");

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("testuser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        AddressResDto result = deliveryAddressService.updateDeliveryAddresses(addressId,addressReqDto,principalDetails);

        // Then
        assertNotNull(result);
        assertEquals("updateHome", result.getDeliveryAddress());


    }

    @Test
    @DisplayName("배송지 정보 수정 실패 - 중복된 배송지 명 존재")
    void testUpdateDeliveryAddressFailDuplicateName(){
        // Given
        AddressReqDto addressReqDto = new AddressReqDto("testHome","new info","202");

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("testuser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->{
            deliveryAddressService.updateDeliveryAddresses(addressId,addressReqDto,principalDetails);
        });


        // Then
        assertEquals("해당 유저의 동일한 배송지가 이미 존재합니다. :" + addressReqDto.getDeliveryAddress(), exception.getMessage());

    }

    @Test
    @DisplayName("배송지 정보 수정 실패 - 권한 없음")
    void testUpdateDeliveryAddressFailNoPermission(){

        // Given
        User updateTestUser = userRepository.save(User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password("encodedPassword")
                .username("updateTestUser")
                .nickname("testnick")
                .role(UserRoles.ROLE_CUSTOMER)
                .deliveryAddresses(new ArrayList<>())
                .build()
        );
        
        DeliveryAddress updateTestAddress = addressRepository.save(
                DeliveryAddress.builder()
                        .deliveryAddressId(UUID.randomUUID())
                        .deliveryAddress("updateTestAddress")
                        .deliveryAddressInfo("Gwanghwamun")
                        .detailAddress("101")
                        .user(updateTestUser) 
                        .build()
        );

        addressRepository.save(updateTestAddress);
        
        UUID updateTestAddressId = updateTestAddress.getDeliveryAddressId();

        AddressReqDto addressReqDto = new AddressReqDto("updateTestHome", "new info", "202");

        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("testuser"); 
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            deliveryAddressService.updateDeliveryAddresses(updateTestAddressId, addressReqDto, principalDetails);
        });

        // Then
        assertEquals("Access denied.", exception.getMessage());

    }

    @Test
    @DisplayName("배송지 삭제 성공")
    void testDeleteDeliveryAddressesSuccess() {

        // Given
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

        when(principalDetails.getUsername()).thenReturn("testuser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        deliveryAddressService.deleteDeliveryAddresses(addressId, principalDetails);

        // Then
        Optional<DeliveryAddress> deleteAddress = addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(addressId);
        assertTrue(deleteAddress.isEmpty());
    }


    @Test
    @DisplayName("배송지 삭제 실패 - 권한 없음")
    void testDeleteDeliveryAddressesFail() {

        // Given
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);

        when(principalDetails.getUsername()).thenReturn("dummyUser");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        // When
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            deliveryAddressService.deleteDeliveryAddresses(addressId, principalDetails);
        });

        // Then
        assertEquals("Access denied.", exception.getMessage());
    }
}