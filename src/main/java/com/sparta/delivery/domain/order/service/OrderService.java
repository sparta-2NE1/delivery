package com.sparta.delivery.domain.order.service;

import com.sparta.delivery.domain.order.dto.OrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

//    public void createOrder(OrderRequestDto requestDto) {
//        //다른 레파지토리 필요. 추후 구현 예정
//        User user = userRepository.findById(requestDto.getUser_id())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
//    }

    public OrderResponseDto getOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문건입니다."));

        return new OrderResponseDto(order);
    }

    public List<OrderResponseDto> getUserOrderList(Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        List<Order> userOrderList = orderRepository.findAllByUserId(user.getUserId());

        if(userOrderList.isEmpty()) {
            throw new IllegalArgumentException("해당 유저에 존재하는 주문건이 없습니다.");
        }

        return userOrderList.stream().map(order -> new OrderResponseDto(order)).toList();
    }

    public List<OrderResponseDto> getStoreOrderList(UUID storeId) {
        //storeID로 store 존재 여부 검증 필요

        List<Order> storeOrderList = orderRepository.findAllByStoreId(storeId);

        if(storeOrderList.isEmpty()) {
            throw new IllegalArgumentException("해당 가게에 존재하는 주문건이 없습니다.");
        }
        return storeOrderList.stream().map(order -> new OrderResponseDto(order)).toList();
    }

    @Transactional
    public void deleteOrder(UUID orderId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문건입니다."));

        order.setDeletedAt(LocalDateTime.now());
        order.setDeletedBy(userId.toString());
    }

    @Transactional
    public OrderResponseDto updateOrder(UUID orderId, Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문건입니다."));

        //주문 수정 로직 추가 예정

        return new OrderResponseDto(order);
    }
}
