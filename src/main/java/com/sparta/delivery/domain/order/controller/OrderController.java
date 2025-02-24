package com.sparta.delivery.domain.order.controller;

import com.sparta.delivery.config.PageableConfig;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.OrderNotFoundException;
import com.sparta.delivery.domain.order.dto.OrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderStatusRequestDto;
import com.sparta.delivery.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    private final PageableConfig pageableConfig;

    @Operation(summary = "주문 등록")
    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        orderService.createOrder(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "단일 주문 검색")
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getOrder(orderId));
    }

    @Operation(summary = "유저 주문 검색 - 가게, 배달지 조건 / 유저 주문 전체 검색")
    @GetMapping("/getUserOrder")
    public ResponseEntity<?> getUserOrderList(@RequestParam(name = "page", required = false) Integer page,
                                              @RequestParam(name = "size", required = false) Integer  size,
                                              @RequestParam(name = "sortBy", required = false) String sortBy,
                                              @RequestParam(name = "orderBy") String orderBy,
                                              @RequestParam(name = "storeIdList", required = false) List<UUID> storeIdList,
                                              @RequestParam(name = "deliveryAddressIdList", required = false) List<UUID> deliveryAddressIdList,
                                              @AuthenticationPrincipal PrincipalDetails userDetails) {
        if (storeIdList == null)
            storeIdList = Collections.emptyList();

        if (deliveryAddressIdList == null)
            deliveryAddressIdList = Collections.emptyList();

        PageRequest pageable = pageableConfig.createPageRequest(page, size, sortBy, orderBy);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getUserOrderList(userDetails.getUsername(), pageable, storeIdList, deliveryAddressIdList));
    }

    @Operation(summary = "가게 주문 조회")
    @GetMapping("/getStoreOrder/{storeId}")
    public ResponseEntity<?> getStoreOrderList(@PathVariable("storeId") UUID storeId,
                                               @RequestParam(name = "page", required = false) Integer page,
                                               @RequestParam(name = "size", required = false) Integer  size,
                                               @RequestParam(name = "sortBy", required = false) String sortBy,
                                               @RequestParam(name = "orderBy") String orderBy,
                                               @AuthenticationPrincipal PrincipalDetails userDetails) {
        Pageable pageable = pageableConfig.createPageRequest(page, size, sortBy, orderBy);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getStoreOrderList(storeId, pageable, userDetails.getUsername()));
    }

    @Operation(summary = "주문 삭제")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        orderService.deleteOrder(orderId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "주문 수정")
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@RequestBody OrderRequestDto requestDto, @PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.updateOrder(requestDto, orderId, userDetails.getUsername()));
    }

    @Operation(summary = "주문 상태 수정 - 사장님만 가능")
    @PostMapping("/updateOrderStatus/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@RequestBody OrderStatusRequestDto requestDto, @PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.updateOrderStatus(orderId, userDetails.getUsername(), requestDto));
    }
}
