package com.sparta.delivery.domain.store.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.dto.StoreResDto;

import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.service.StoreService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private final StoreService storeService;

    @PostMapping("/") // 가게 등록
    public ResponseEntity<Object>
    register(@RequestBody @Valid StoreReqDto storeReqDto, BindingResult bindingResult) {
        Map<String,String> errorSave = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorSave.put("error-field : "+fieldError.getField(), "message : "+fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).body(errorSave);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(storeService.storeCreate(storeReqDto));
    }




}
