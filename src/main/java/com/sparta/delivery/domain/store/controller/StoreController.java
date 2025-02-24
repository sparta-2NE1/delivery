package com.sparta.delivery.domain.store.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.service.StoreService;
import com.sparta.delivery.domain.store.swagger.StoreSwaggerDocs;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/store")
@RequiredArgsConstructor
@RestController
@Validated
public class StoreController {

    private final StoreService storeService;

    @StoreSwaggerDocs.Register
    @PostMapping("")
    public ResponseEntity<?>
    register(@RequestBody @Valid StoreReqDto storeReqDto, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails userDetails) {
        Map<String, String> errorRegister = new HashMap<>();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorRegister.put("error-field : " + fieldError.getField(), "message : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRegister);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.storeCreate(storeReqDto, userDetails));
    }

    @StoreSwaggerDocs.StoreList
    @GetMapping("")
    public ResponseEntity<?> storeList(@PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.getStoreList(pageable));
    }

    @StoreSwaggerDocs.StoreOne
    @GetMapping("/{storeId}")
    public ResponseEntity<?> storeOne(@PathVariable UUID storeId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.getStoreOne(storeId));
    }

    @StoreSwaggerDocs.StoreSearch
    @GetMapping("/search")
    public ResponseEntity<?>
    storeSearch(@RequestParam String keyword, @RequestParam @Pattern(regexp = "한식|중식|분식|치킨|피자", message = "유효하지 않은 카테고리입니다.") String category,
                @PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.searchStore(keyword, pageable, category));
    }

    @StoreSwaggerDocs.StoreUpdate
    @PutMapping("/{storeId}")
    public ResponseEntity<?>
    storeUpdate(@PathVariable UUID storeId, @RequestBody @Valid StoreReqDto storeReqDto, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, String> errorUpdate = new HashMap<String, String>();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorUpdate.put("error-field : " + fieldError.getField(), "message : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUpdate);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.updateStore(storeReqDto, storeId, principalDetails));
    }

    @StoreSwaggerDocs.StoreDelete
    @PatchMapping("/{storeId}")
    public void // 가게 삭제
    storeDelete(@PathVariable UUID storeId, @AuthenticationPrincipal PrincipalDetails userDetails) {
        storeService.deleteStore(storeId, userDetails);
    }


}
