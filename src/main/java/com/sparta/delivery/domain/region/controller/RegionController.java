package com.sparta.delivery.domain.region.controller;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.region.dto.RegionReqDto;
import com.sparta.delivery.domain.region.dto.RegionResDto;
import com.sparta.delivery.domain.region.service.RegionService;
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

    @PostMapping("")
    public ResponseEntity<?> // 운영 지역 등록
    register(@RequestBody @Valid RegionReqDto regionReqDto, BindingResult bindingResult) {
        Map<String, Object> errorSave = new HashMap<>();
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorSave.put("error-field : " + fieldError.getField(), "message : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorSave);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(regionService.regionCreate(regionReqDto));
    }

    @GetMapping("")//운영 지역 리스트 조회
    public ResponseEntity<?> regionList(@PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getsRegionList(pageable));
    }

    @GetMapping("/{regionId}")//운영 지역 단일 조회
    public ResponseEntity<RegionResDto> regionOne(@PathVariable UUID regionId) {
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getRegionOne(regionId));
    }

    @GetMapping("/search")//운영 지역 검색
    public ResponseEntity<List<RegionResDto>> regionSearch(@RequestParam String keyword, @PageableDefault(page = 0, size = 10, sort = {"createdAt", "updatedAt"}) Pageable pageable) {
        List<Integer> Size_List = List.of(10, 20, 30);
        if (!Size_List.contains((pageable.getPageSize()))) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }
        return ResponseEntity.status(HttpStatus.OK).body(regionService.searchRegion(keyword, pageable));
    }

    @PutMapping("/{regionId}")//운영 지역 수정
    public ResponseEntity<?> regionUpdate
            (@PathVariable UUID regionId, @RequestBody @Valid RegionReqDto regionReqDto, BindingResult bindingResult) {
        Map<String, Object> errorsave2 = new HashMap<String, Object>();

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsave2);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(regionService.updateRegion(regionReqDto, regionId));
    }

    @DeleteMapping("/{regionId}")//운영 지역 삭제
    public void regionDelete(@PathVariable UUID regionId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        regionService.deleteRegion(regionId, principalDetails.getUsername());
    }


}
