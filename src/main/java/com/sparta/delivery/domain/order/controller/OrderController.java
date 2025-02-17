package com.sparta.delivery.domain.order.controller;

import com.sparta.delivery.domain.order.dto.OrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

//    @PostMapping()
//    public void createOrder() {
//        orderService.createOrder();
//    }

    @GetMapping("/{order_id}")
    public OrderResponseDto getOrder(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping
    public List<OrderResponseDto> getUserOrderList(Long userId) {
        //추후 유저아이디 받는 방식 변경 예정
        return orderService.getUserOrderList(userId);
    }

    @GetMapping("/{store_id}")
    public List<OrderResponseDto> getStoreOrderList(@PathVariable UUID storeId) {
        return orderService.getStoreOrderList(storeId);
    }

    @DeleteMapping("/{order_id}")
    public void deleteOrder(@PathVariable UUID orderId, Long userId) {
        orderService.deleteOrder(orderId, userId);
    }

    @PutMapping("/{order_id}")
    public OrderResponseDto updateOrder(@RequestBody OrderRequestDto requestDto, @PathVariable UUID orderId, Long user_id) {
        return orderService.updateOrder(orderId, user_id);
    }
}
