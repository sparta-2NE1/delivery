package com.sparta.delivery.domain.store.dto;

import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.store.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StoreResDto {

    private String name;

    private String address;

    private boolean status;

    private List<Region> regionList = new ArrayList<>();

    private Category category;


    public StoreResDto(Stores store) {
        this.name = store.getName();
        this.address = store.getAddress();
        this.status = store.isStatus();
        this.regionList = store.getRegionList();
        this.category = store.getCategory();
    }



}
