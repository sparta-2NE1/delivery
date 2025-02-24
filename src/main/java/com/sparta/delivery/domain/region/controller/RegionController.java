package com.sparta.delivery.domain.region.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.region.dto.RegionReqDto;
import com.sparta.delivery.domain.region.dto.RegionResDto;
import com.sparta.delivery.domain.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RequestMapping("/api/region")
@RequiredArgsConstructor
@RestController
public class RegionController {


    private final RegionService regionService;

    @Operation(summary = "운영 지역 등록")
    @PostMapping("")
    public ResponseEntity<?>
    register(@RequestBody @Valid RegionReqDto regionReqDto, BindingResult bindingResult, @AuthenticationPrincipal PrincipalDetails userDetails) {
        Map<String, Object> errorSave = new HashMap<>();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorSave.put("error-field : " + fieldError.getField(), "message : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorSave);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(regionService.regionCreate(regionReqDto, userDetails));
    }
    @Operation(summary = "특정 가게 운영 지역 리스트 조회")
    @GetMapping("/{storeId}")
    public ResponseEntity<?> regionList(@PathVariable UUID storeId, @PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getRegionList(pageable, storeId));
    }

    @Operation(summary = "운영 지역 리스트 조회")
    @GetMapping("")
    public ResponseEntity<?> regionList(@PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getAllRegionList(pageable));
    }
    @Operation(summary = "운영 지역 검색")
    @GetMapping("/search")
    public ResponseEntity<List<RegionResDto>> regionSearch(@RequestParam String keyword, @PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(regionService.searchRegion(keyword, pageable));
    }
    @Operation(summary = "운영 지역 수정")
    @PutMapping("/{regionId}")
    public ResponseEntity<?> regionUpdate
            (@PathVariable UUID regionId, @RequestBody @Valid RegionReqDto regionReqDto, BindingResult bindingResult,
             @AuthenticationPrincipal PrincipalDetails userDetails) {
        Map<String, Object> errorUpdate = new HashMap<>();

        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorUpdate.put("error-field : " + fieldError.getField(), "message : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorUpdate);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(regionService.updateRegion(regionReqDto, regionId, userDetails));
    }

    @Operation(summary = "운영 지역 삭제")
    @PatchMapping("/{regionId}")//운영 지역 삭제
    public void regionDelete(@PathVariable UUID regionId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        regionService.deleteRegion(regionId, principalDetails.getUsername());
    }



}
