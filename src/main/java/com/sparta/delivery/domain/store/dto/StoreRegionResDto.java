package com.sparta.delivery.domain.store.dto;
import com.sparta.delivery.domain.region.dto.RegionListDto;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
@AllArgsConstructor
@Builder
public class StoreRegionResDto {

    private String name;

    private String address;

    private boolean status;

    private List<RegionListDto> regionList = new ArrayList<>();

    private Category category;


    public StoreRegionResDto(Stores store) {
        this.name = store.getName();
        this.address = store.getAddress();
        this.status = store.isStatus();
        this.regionList = store.getRegionList() != null ? store.getRegionList().stream().map(RegionListDto::new).
                collect(Collectors.toList()) : new ArrayList<>();
        this.category = store.getCategory();
    }

}
