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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    private final RegionService regionService;

    @PostMapping("/")
    public ResponseEntity<?> // 운영 지역 등록
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
    public ResponseEntity<?> regionList(@PageableDefault(page=0,size=10,sort={"createdAt","updatedAt"}) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getsRegionList(pageable));
    }


    @GetMapping("/{region_id}")//운영 지역 단일 조회
    public ResponseEntity<RegionResDto> regionOne(@PathVariable UUID region_id){
        return ResponseEntity.status(HttpStatus.OK).body(regionService.getRegionOne(region_id));
    }


    @GetMapping("/search")//운영 지역 검색
    public ResponseEntity<List<RegionResDto>> regionSearch(@RequestParam String keyword,@PageableDefault(page=0,size=10,sort={"createdAt","updatedAt"})Pageable pageable){
        List<Integer> Size_List = List.of(10,20,30);//SIZE크기제한
        if(!Size_List.contains((pageable.getPageSize()))){//10건,20건,30건이 사이즈오면 제한하고 10건으로 고정)
            pageable = PageRequest.of(pageable.getPageNumber(), 10,pageable.getSort()); }

        return ResponseEntity.status(HttpStatus.OK).body(regionService.searchRegion(keyword,pageable));
    }

    @PutMapping("/{region_id}")//운영 지역 수정
    public ResponseEntity<?> regionUdate
            (@PathVariable UUID region_id, @RequestBody @Valid RegionReqDto regionReqDto, BindingResult bindingResult){
        Map<String,Object> errorsave2 = new HashMap<String, Object>();

        if (bindingResult.hasErrors()) { //name,address,status notnull - 테스트필요
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorsave2);
        }
        //반환값 StoreResDto
        return ResponseEntity.status(HttpStatus.OK)
                .body(regionService.updateRegion(regionReqDto,region_id));

    }

    @DeleteMapping("/{region_id}")//운영 지역 삭제
    public void regionDelete(@PathVariable UUID region_id,@AuthenticationPrincipal PrincipalDetails principalDetails){
        regionService.deleteRegion(region_id,principalDetails.getUsername());
    }


}
