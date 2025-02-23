package com.sparta.delivery.domain.store.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.ForbiddenException;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.dto.StoreResDto;

import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import com.sparta.delivery.domain.store.service.StoreService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/store")
@RequiredArgsConstructor
@RestController
public class StoreController {

    private final StoreService storeService;

    @PostMapping("") // 가게 등록
    public ResponseEntity<?>
    register(@RequestBody @Valid StoreReqDto storeReqDto, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails userDetails) {
        Map<String, String> errorSave = new HashMap<>();
        System.out.println(userDetails.getRole() + "이닷");
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorSave.put("error-field : " + fieldError.getField(), "message : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorSave);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.storeCreate(storeReqDto, userDetails));
    }

    @GetMapping("") //가게 리스트 조회
    public ResponseEntity<?> storeList(@PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.getStoreList(pageable));
    }

    @GetMapping("/{storeId}")// 가게 단일조회
    public ResponseEntity<?> storeOne(@PathVariable UUID storeId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.getStoreOne(storeId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> // 가게 검색
    storeSearch(@RequestParam String keyword, @RequestParam Category category, @PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.searchStore(keyword, pageable, category));
    }


    @PutMapping("/{storeId}")
    public ResponseEntity<?> // 가게 업데이트
    storeUpdate(@PathVariable UUID storeId, @RequestBody @Valid StoreReqDto storeReqDto, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Map<String, String> errorSave2 = new HashMap<String, String>();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorSave2.put("error-field : " + fieldError.getField(), "message : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorSave2);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(storeService.updateStore(storeReqDto, storeId, principalDetails));
    }

    @PatchMapping("/{storeId}")
    public void // 가게 삭제
    storeDelete(@PathVariable UUID storeId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        storeService.deleteStore(storeId, principalDetails.getUsername());
    }


}
