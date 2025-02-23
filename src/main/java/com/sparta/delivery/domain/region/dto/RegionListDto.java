package com.sparta.delivery.domain.region.dto;


import com.sparta.delivery.domain.region.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegionListDto {

    private UUID storeId;

    private String province;

    private String city;

    private String locality;


    public RegionListDto(Region region) {
        this.province = region.getProvince();
        this.city = region.getCity();
        this.locality = region.getLocality();
        this.storeId = region.getStores() != null ? region.getStores().getStoreId() : null; // storeId 값 설정

    }
}
