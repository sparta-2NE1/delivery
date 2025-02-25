package com.sparta.delivery.paymentTest;

import com.sparta.delivery.domain.card.entity.Card;
import com.sparta.delivery.domain.card.repository.CardRepository;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.orderProduct.entity.OrderProduct;
import com.sparta.delivery.domain.orderProduct.repository.OrderProductRepository;
import com.sparta.delivery.domain.payment.dto.PaymentDto;
import com.sparta.delivery.domain.payment.dto.SearchDto;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.payment.repository.PaymentRepository;
import com.sparta.delivery.domain.payment.service.PaymentService;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.product.repository.ProductRepository;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.repository.StoreRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.enums.UserRoles;
import com.sparta.delivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // application-test.yml을 기준으로 test 실행 환경을 구성합니다.
@SpringBootTest  // Spring 컨텍스트를 로드하여 통합 테스트 수행
@Transactional  // 각 테스트 후 롤백하여 데이터 정합성 유지
public class PaymentServiceIntegrationTest {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DeliveryAddressRepository addressRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    private User testUser;
    private UUID cardId;

    private Order testOrder;
    private UUID orderId;
    private Card testCard;
    private Payment testPayment;
    private UUID paymentId;

    private DeliveryAddress testDeliveryAddress;

    private OrderProduct orderProduct;

    private Product product1;
    private Stores store1;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .username("testuser")
                .nickname("testnick")
                .role(UserRoles.ROLE_CUSTOMER)
                .build();

        testUser = userRepository.save(testUser);
        cardId = UUID.randomUUID();
        cardId = UUID.randomUUID();
        testCard = Card.builder()
                .cardId(cardId)
                .cardCompany("국민")
                .cardNumber("1234")
                .cardName("국민카드")
                .user(testUser)
                .build();
        testCard = cardRepository.save(testCard);
        cardId = testCard.getCardId();

        store1 = Stores.builder()
                .storeId(UUID.randomUUID())
                .name("testStore1")
                .address("Gwanghwamun")
                .status(true)
                .user(testUser)
                .build();
        store1 = storeRepository.save(store1);

        product1 = Product.builder()
                .productId(UUID.randomUUID())
                .store(store1)
                .name("product1")
                .description("yummy~")
                .price(11000)
                .quantity(20)
                .hidden(false)
                .build();
        product1 = productRepository.save(product1);

        testDeliveryAddress = DeliveryAddress.builder()
                .deliveryAddressId(UUID.randomUUID())
                .deliveryAddress("testHome")
                .deliveryAddressInfo("Gwanghwamun")
                .detailAddress("101")
                .user(testUser)
                .build();

        testDeliveryAddress = addressRepository.save(testDeliveryAddress);


        orderId = UUID.randomUUID();
        testOrder = Order.builder()
                .orderId(orderId)
                .orderTime(LocalDateTime.now())
                .orderType(OrderType.DELIVERY)
                .orderStatus(OrderStatus.PAYMENT_WAIT)
                .user(testUser)
                .stores(store1)
                .deliveryAddress(testDeliveryAddress)
                .build();

        testOrder = orderRepository.save(testOrder);
        orderId = testOrder.getOrderId();

        orderProduct = OrderProduct.builder()
                .orderProductId(UUID.randomUUID())
                .order(testOrder)
                .product(product1)
                .build();
        orderProduct = orderProductRepository.save(orderProduct);

        paymentId = UUID.randomUUID();
        testPayment = Payment.builder()
                .paymentId(paymentId)
                .user(testUser)
                .card(testCard)
                .order(testOrder)
                .amount(10000)
                .build();
        testPayment = paymentRepository.save(testPayment);
        paymentId = testPayment.getPaymentId();
    }


    @Test
    @DisplayName("결제 내역 조회 성공")
    void testGetPaymentSuccess() {
        PaymentDto result = paymentService.getPayment(paymentId, testUser.getUsername());
        assertNotNull(result);
    }

    @Test
    @DisplayName("결제 내역 조회 실패 : 결제 내역 없음")
    void testGetPaymentFailNotFound() {
        paymentService.deletePayment(paymentId, testUser.getUsername());
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> paymentService.getPayment(paymentId, testUser.getUsername()));
        assertEquals("결제 내역이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("결제 내역 검색 성공")
    void testSearchPaymentsSuccess() {
        SearchDto searchDto = new SearchDto();
        List<PaymentDto> searchResult = paymentService.searchPayments(searchDto, testUser.getUsername());
        int size = searchResult.size();
        assertEquals(1, size);
    }

    @Test
    @DisplayName("최소 금액이 30,000인 결제 내역 검색 성공")
    void testConditionSearchPaymentsSuccess() {
        SearchDto searchDto = new SearchDto(30000, null, null, null, null, null);
        List<PaymentDto> searchResult = paymentService.searchPayments(searchDto, testUser.getUsername());
        int size = searchResult.size();
        assertEquals(0, size);
    }

    @Test
    @DisplayName("결제 내역 삭제 성공")
    void testDeletePaymentSuccess() {
        paymentService.deletePayment(paymentId, testUser.getUsername());
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> paymentService.getPayment(paymentId, testUser.getUsername()));
        assertEquals("결제 내역이 존재하지 않습니다.", exception.getMessage());
    }

}

