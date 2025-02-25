package com.sparta.delivery.ReviewTest;

import com.sparta.delivery.config.PageableConfig;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.ReviewAlreadyExistsException;
import com.sparta.delivery.config.global.exception.custom.ReviewNotAllowedException;
import com.sparta.delivery.config.global.exception.custom.ReviewNotFoundException;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.dto.ReviewUpdateRequestDto;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.review.service.ReviewService;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test") // application-test.yml을 기준으로 test 실행 환경을 구성합니다.
@SpringBootTest  // Spring 컨텍스트를 로드하여 통합 테스트 수행
@Transactional  // 각 테스트 후 롤백하여 데이터 정합성 유지
public class ReviewServiceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PageableConfig pageableConfig;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    private User customer;
    private User owner;
    private Stores store1;
    private Stores store2;
    private Order order1;
    private Order order2;
    //리뷰등록 테스트용
    private Order order3;
    //주문완료 안된 주문
    private Order order4;
    private Review review1;
    private Review review2;


    @BeforeEach
    void setUp() {
        customer = User.builder()
                .userId(UUID.randomUUID())
                .email("customer@example.com")
                .password("encodedPassword")
                .username("customer")
                .nickname("customer")
                .role(UserRoles.ROLE_CUSTOMER)
                .deliveryAddresses(new ArrayList<>())
                .build();

        owner = User.builder()
                .userId(UUID.randomUUID())
                .email("owner@example.com")
                .password("encodedPassword")
                .username("owner")
                .nickname("owner")
                .role(UserRoles.ROLE_OWNER)
                .deliveryAddresses(new ArrayList<>())
                .build();

        customer = userRepository.save(customer);
        owner = userRepository.save(owner);

        store1 = Stores.builder()
                .storeId(UUID.randomUUID())
                .name("testStore1")
                .address("Gwanghwamun")
                .status(true)
                .user(owner)
                .build();
        store1 = storeRepository.save(store1);

        store2 = Stores.builder()
                .storeId(UUID.randomUUID())
                .name("testStore2")
                .address("Gwanghwamun")
                .status(true)
                .user(owner)
                .build();
        store2 = storeRepository.save(store2);

        order1 = Order.builder()
                .orderId(UUID.randomUUID())
                .orderTime(LocalDateTime.now())
                .orderType(OrderType.PACKAGING)
                .orderStatus(OrderStatus.ORDER_COMPLETE)
                .requirements("test order")
                .stores(store1)
                .user(customer)
                .build();
        order1 = orderRepository.save(order1);

        order2 = Order.builder()
                .orderId(UUID.randomUUID())
                .orderTime(LocalDateTime.now())
                .orderType(OrderType.PACKAGING)
                .orderStatus(OrderStatus.ORDER_COMPLETE)
                .requirements("test order")
                .stores(store1)
                .user(customer)
                .build();
        order2 = orderRepository.save(order2);

        order3 = Order.builder()
                .orderId(UUID.randomUUID())
                .orderTime(LocalDateTime.now())
                .orderType(OrderType.PACKAGING)
                .orderStatus(OrderStatus.ORDER_COMPLETE)
                .requirements("test order")
                .stores(store1)
                .user(customer)
                .build();
        order3 = orderRepository.save(order3);

        order4 = Order.builder()
                .orderId(UUID.randomUUID())
                .orderTime(LocalDateTime.now())
                .orderType(OrderType.PACKAGING)
                .orderStatus(OrderStatus.ORDER_IN)
                .requirements("test order")
                .stores(store1)
                .user(customer)
                .build();
        order4 = orderRepository.save(order4);

        review1 = Review.builder()
                .reviewId(UUID.randomUUID())
                .comment("test review 1")
                .star(4)
                .order(order1)
                .user(customer)
                .stores(store1)
                .build();

        review2 = Review.builder()
                .reviewId(UUID.randomUUID())
                .comment("test review 2")
                .star(4)
                .order(order2)
                .user(customer)
                .stores(store1)
                .build();
        review1 = reviewRepository.save(review1);
        review2 = reviewRepository.save(review2);
    }

    @Test
    @DisplayName("리뷰 등록 성공")
    void testCreateReviewSuccess() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto("test review 3", 2, order3.getOrderId());

        Review result = reviewService.createReview(reviewRequestDto, principalDetails.getUsername());

        assertNotNull(result);
        assertEquals("test review 3", result.getComment());
        assertEquals(2, result.getStar());
    }

    @Test
    @DisplayName("리뷰 등록 실패 - 이미 작성이 완료된 주문")
    void testCreateReviewFailWhenReviewAlreadyExist() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto("test review 3", 2, order2.getOrderId());

        ReviewAlreadyExistsException exception = assertThrows(ReviewAlreadyExistsException.class, () -> {
            reviewService.createReview(reviewRequestDto, principalDetails.getUsername());
        });
        assertEquals("이미 리뷰 작성을 완료한 주문입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("리뷰 등록 실패 - 주문이 완료되지 않음")
    void testCreateReviewFailWhenOrderNotCompleted() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        ReviewRequestDto reviewRequestDto = new ReviewRequestDto("test review 4", 2, order4.getOrderId());

        ReviewNotAllowedException exception = assertThrows(ReviewNotAllowedException.class, () -> {
            reviewService.createReview(reviewRequestDto, principalDetails.getUsername());
        });
        assertEquals("주문이 모두 완료되었을 경우 리뷰 작성이 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("유저 리뷰 조회 성공")
    void testSearchUserReviewSuccess() {
        for(int i = 0; i < 12; i++) {
            Order dummyOrder = Order.builder()
                    .orderId(UUID.randomUUID())
                    .orderTime(LocalDateTime.now())
                    .orderType(OrderType.PACKAGING)
                    .orderStatus(OrderStatus.ORDER_COMPLETE)
                    .requirements("test order" + i)
                    .stores(store1)
                    .user(customer)
                    .build();
            dummyOrder = orderRepository.save(dummyOrder);

            Review dummyReview = Review.builder()
                    .reviewId(UUID.randomUUID())
                    .comment("test review" + i)
                    .star(3)
                    .order(dummyOrder)
                    .user(customer)
                    .stores(store1)
                    .build();
            reviewRepository.save(dummyReview);
        }
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);

        Page<ReviewResponseDto> result = reviewService.getUserReview(principalDetails.getUsername(), pageRequest);
        assertNotNull(result);
        assertEquals(14 , result.getTotalElements());
    }

    @Test
    @DisplayName("유저 리뷰 조회 성공 - 리뷰 없음")
    void testSearchUserReviewSuccessWhenReviewIsEmpty() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("owner");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_OWNER);

        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.getUserReview(principalDetails.getUsername(), pageRequest);
        });
        assertEquals("로그인한 사용자가 작성한 리뷰가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("가게 리뷰 조회 성공")
    void testSearchStoreReviewSuccess() {
        for(int i = 0; i < 12; i++) {
            Order dummyOrder = Order.builder()
                    .orderId(UUID.randomUUID())
                    .orderTime(LocalDateTime.now())
                    .orderType(OrderType.PACKAGING)
                    .orderStatus(OrderStatus.ORDER_COMPLETE)
                    .requirements("test order" + i)
                    .stores(store1)
                    .user(customer)
                    .build();
            dummyOrder = orderRepository.save(dummyOrder);

            Review dummyReview = Review.builder()
                    .reviewId(UUID.randomUUID())
                    .comment("test review" + i)
                    .star(3)
                    .order(dummyOrder)
                    .user(customer)
                    .stores(store1)
                    .build();
            reviewRepository.save(dummyReview);
        }
        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);
        List<Integer> starList = new ArrayList<>();
        starList.add(3);

        Page<ReviewResponseDto> result = reviewService.getStoreReviewSearch(store1.getStoreId(), starList, pageRequest);
        assertNotNull(result);
        assertEquals(12 , result.getTotalElements());
    }

    @Test
    @DisplayName("가게 리뷰 조회 성공 - 리뷰 없음")
    void testSearchStoreReviewSuccessWhenReviewIsEmpty() {
        PageRequest pageRequest = pageableConfig.createPageRequest(null, null, null, null);
        List<Integer> starList = Collections.emptyList();

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.getStoreReviewSearch(store2.getStoreId(), starList, pageRequest);
        });
        assertEquals("해당 가게에 작성된 리뷰가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void testDeleteReviewSuccess() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        Review result = reviewService.deleteReview(review1.getReviewId(), principalDetails.getUsername());
        assertNotNull(result);
        assertEquals(principalDetails.getUsername(), result.getDeletedBy());
    }

    @Test
    @DisplayName("리뷰 삭제 실패 - 잘못된 리뷰 ID")
    void testDeleteReviewFailWhenReviewIdWrong(){
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.deleteReview(UUID.randomUUID(), principalDetails.getUsername());
        });
        assertEquals("존재하지 않거나 현재 로그인한 사용자의 리뷰가 아닙니다.", exception.getMessage());
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void testUpdateReviewSuccess() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        ReviewUpdateRequestDto reviewUpdateRequestDto = new ReviewUpdateRequestDto("update review", 3);
        ReviewResponseDto result = reviewService.updateReview(review2.getReviewId(), reviewUpdateRequestDto, principalDetails.getUsername());

        assertNotNull(result);
        assertEquals("update review", result.getComment());
        assertEquals(3, result.getStar());
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 잘못된 리뷰 ID")
    void testUpdateReviewWhenReviewIdWrong() {
        PrincipalDetails principalDetails = mock(PrincipalDetails.class);
        when(principalDetails.getUsername()).thenReturn("customer");
        when(principalDetails.getRole()).thenReturn(UserRoles.ROLE_CUSTOMER);

        ReviewUpdateRequestDto reviewUpdateRequestDto = new ReviewUpdateRequestDto("update review", 4);

        ReviewNotFoundException exception = assertThrows(ReviewNotFoundException.class, () -> {
            reviewService.updateReview(UUID.randomUUID(), reviewUpdateRequestDto, principalDetails.getUsername());
        });
        assertEquals("존재하지 않거나 현재 로그인한 사용자의 리뷰가 아닙니다.", exception.getMessage());
    }
}
