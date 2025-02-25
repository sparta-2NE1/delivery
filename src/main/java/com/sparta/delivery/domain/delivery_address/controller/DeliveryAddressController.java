package com.sparta.delivery.domain.delivery_address.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.delivery_address.dto.AddressReqDto;
import com.sparta.delivery.domain.delivery_address.dto.AddressSearchDto;
import com.sparta.delivery.domain.delivery_address.service.DeliveryAddressService;
import com.sparta.delivery.domain.delivery_address.swagger.DeliveryAddressSwaggerDocs;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name ="DeliveryAddress API", description = "배송지 관련 API")
@RestController
@RequestMapping("/api/delivery-addresses")
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    @DeliveryAddressSwaggerDocs.addAddress
    @PostMapping
    public ResponseEntity<?> addAddress(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @Valid @RequestBody AddressReqDto addressReqDto,
                                        BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ValidationErrorResponse(bindingResult));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(deliveryAddressService.addAddress(addressReqDto, principalDetails));
    }

    @DeliveryAddressSwaggerDocs.getAddress
    @GetMapping("/{id}")
    public ResponseEntity<?> getDeliveryAddressById(@PathVariable("id") UUID id){

        return ResponseEntity.status(HttpStatus.OK)
                .body(deliveryAddressService.getDeliveryAddressById(id));
    }

    @DeliveryAddressSwaggerDocs.SearchAddress
    @GetMapping
    public ResponseEntity<?> getDeliveryAddresses(@RequestBody AddressSearchDto addressSearchDto){

        return ResponseEntity.status(HttpStatus.OK)
                .body(deliveryAddressService.getDeliveryAddresses(addressSearchDto));
    }

    @DeliveryAddressSwaggerDocs.UpdateAddress
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateDeliveryAddresses(@PathVariable("id") UUID id,
                                                     @AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @Valid @RequestBody AddressReqDto addressReqDto,
                                                     BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ValidationErrorResponse(bindingResult));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(deliveryAddressService.updateDeliveryAddresses(id,addressReqDto,principalDetails));
    }

    @DeliveryAddressSwaggerDocs.DeleteAddress
    @PatchMapping("/{id}/delete")
    public ResponseEntity<?> deleteDeliveryAddresses(@PathVariable("id") UUID id,
                                                     @AuthenticationPrincipal PrincipalDetails principalDetails){
        deliveryAddressService.deleteDeliveryAddresses(id,principalDetails);
        return ResponseEntity.status(HttpStatus.OK).build();
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
