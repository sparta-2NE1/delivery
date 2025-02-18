package com.sparta.delivery.domain.order.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.order.dto.OrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderStatusRequestDto;
import com.sparta.delivery.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("")
    public void createOrder(@RequestBody OrderRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        orderService.createOrder(requestDto, userDetails.getUsername());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrder(orderId));
    }

    @GetMapping("/getUserOrder")
    public ResponseEntity<?> getUserOrderList(@PageableDefault(size = 10, page = 0, sort = "createdAt",
                                                      direction = Sort.Direction.DESC) Pageable pageable,
                                              @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getUserOrderList(userDetails.getUsername(), pageable));
    }

    @GetMapping("/getStoreOrder/{storeId}")
    public ResponseEntity<?> getStoreOrderList(@PathVariable("storeId") UUID storeId,
                                               @PageableDefault(size = 10, page = 0, sort = "createdAt",
                                                       direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getStoreOrderList(storeId, pageable));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.deleteOrder(orderId, userDetails.getUsername()));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@RequestBody OrderRequestDto requestDto, @PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.updateOrder(requestDto, orderId, userDetails.getUsername()));
    }

    @PostMapping("/updateOrderStatus/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@RequestBody OrderStatusRequestDto requestDto, @PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.updateOrderStatus(orderId, userDetails.getUsername(), requestDto));
    }
}
