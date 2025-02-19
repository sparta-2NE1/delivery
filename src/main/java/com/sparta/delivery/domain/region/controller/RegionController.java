package com.sparta.delivery.domain.region.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.region.dto.RegionReqDto;
import com.sparta.delivery.domain.region.dto.RegionResDto;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import com.sparta.delivery.domain.region.service.RegionService;
import com.sparta.delivery.domain.store.dto.StoreReqDto;
import com.sparta.delivery.domain.store.dto.StoreResDto;
import com.sparta.delivery.domain.store.entity.Stores;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RequestMapping("/api/region")
@RequiredArgsConstructor
@RestController
public class RegionController {

    @Autowired
    private final RegionService regionService;

    @PostMapping("/")
    public ResponseEntity<Object> // 운영 지역 등록
    register(@RequestHeader("Authorization") String authorizationHeader,
             @RequestBody @Valid RegionReqDto regionReqDto, BindingResult bindingResult) {
        Map<String,Object> errorSave = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorSave.put("error-field : "+fieldError.getField(), "message : "+fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorSave);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(regionService.regionCreate(regionReqDto));
    }

    @GetMapping("/")//운영 지역 리스트 조회
    public ResponseEntity<List<RegionResDto>> regionList(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(regionService.getSRegionList());
    }

    @GetMapping("/{region_id}")//운영 지역 단일 조회
    public ResponseEntity<RegionResDto> regionOne(@PathVariable UUID region_id){
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getRegionOne(region_id));
    }
}
