package com.sparta.delivery.domain.region.dto;

import com.sparta.delivery.domain.region.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class RegionResDto {

    private String province;

    private String city;

    private String locality;

//  private Stores store;

    public RegionResDto(Region region) {
        this.province = region.getProvince();
        this.city = region.getCity();
        this.locality = region.getLocality();

    }
}
