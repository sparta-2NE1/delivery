package com.sparta.delivery.domain.delivery_address.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.delivery_address.dto.AddressReqDto;
import com.sparta.delivery.domain.delivery_address.service.DeliveryAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delivery-addresses")
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    @PostMapping
    public ResponseEntity<?> addAddress(@Valid @RequestBody AddressReqDto addressReqDto,
                                        BindingResult bindingResult ,
                                        @AuthenticationPrincipal PrincipalDetails principalDetails){

        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ValidationErrorResponse(bindingResult));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(deliveryAddressService.addAddress(addressReqDto, principalDetails));
    }

    private Map<String, Object> ValidationErrorResponse(BindingResult bindingResult) {
        List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "message", fieldError.getDefaultMessage(),
                        "rejectedValue", String.valueOf(fieldError.getRejectedValue()) // 입력된 값도 포함
                ))
                .toList();

        return Map.of(
                "status", 400,
                "error", "Validation Field",
                "message", errors
        );
    }
}
