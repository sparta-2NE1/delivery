package com.sparta.delivery.domain.order.controller;

import com.sparta.delivery.config.PageableConfig;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.order.dto.OrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderStatusRequestDto;
import com.sparta.delivery.domain.order.service.OrderService;
import com.sparta.delivery.domain.order.swagger.OrderSwaggerDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name ="Order API", description = "주문 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    private final PageableConfig pageableConfig;

    @OrderSwaggerDocs.addOrder
    @Operation(summary = "주문 등록")
    @PostMapping("")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequestDto requestDto, @AuthenticationPrincipal PrincipalDetails userDetails) {
        requestDto.isValidDeliveryAddress();
        orderService.createOrder(requestDto, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @OrderSwaggerDocs.getOrder
    @Operation(summary = "단일 주문 검색")
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getSingleOrder(orderId));
    }

    @OrderSwaggerDocs.getUserOrder
    @Operation(summary = "유저 주문 검색 - 가게, 배달지 조건 / 유저 주문 전체 검색")
    @GetMapping("/getUserOrder")
    public ResponseEntity<?> getUserOrderList(@RequestParam(name = "page", required = false) Integer page,
                                              @RequestParam(name = "size", required = false) Integer  size,
                                              @RequestParam(name = "sortBy", required = false) String sortBy,
                                              @RequestParam(name = "orderBy", required = false) String orderBy,
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

    @OrderSwaggerDocs.getStoreOrder
    @Operation(summary = "가게 주문 조회")
    @GetMapping("/getStoreOrder/{storeId}")
    public ResponseEntity<?> getStoreOrderList(@PathVariable("storeId") UUID storeId,
                                               @RequestParam(name = "page", required = false) Integer page,
                                               @RequestParam(name = "size", required = false) Integer  size,
                                               @RequestParam(name = "sortBy", required = false) String sortBy,
                                               @RequestParam(name = "orderBy", required = false) String orderBy,
                                               @AuthenticationPrincipal PrincipalDetails userDetails) {
        Pageable pageable = pageableConfig.createPageRequest(page, size, sortBy, orderBy);
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.getStoreOrderList(storeId, pageable, userDetails.getUsername()));
    }

    @OrderSwaggerDocs.deleteOrder
    @Operation(summary = "주문 삭제")
    @PatchMapping("/deleteOrder/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        orderService.deleteOrder(orderId, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @OrderSwaggerDocs.updateOrder
    @Operation(summary = "주문 수정")
    @PatchMapping("/updateOrder/{orderId}")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody OrderRequestDto requestDto, @PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.updateOrder(requestDto, orderId, userDetails.getUsername()));
    }

    @OrderSwaggerDocs.updateOrderStatus
    @Operation(summary = "주문 상태 수정 - 사장님만 가능")
    @PatchMapping("/updateOrderStatus/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@Valid @RequestBody OrderStatusRequestDto requestDto, @PathVariable("orderId") UUID orderId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.updateOrderStatus(orderId, userDetails.getUsername(), requestDto));
    }
}
