package com.sparta.delivery.domain.delivery_address.service;

import com.querydsl.core.BooleanBuilder;
import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.config.global.exception.custom.DeliveryAddressNotFoundException;
import com.sparta.delivery.domain.delivery_address.dto.AddressReqDto;
import com.sparta.delivery.domain.delivery_address.dto.AddressResDto;
import com.sparta.delivery.domain.delivery_address.dto.AddressSearchDto;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.delivery_address.entity.QDeliveryAddress;
import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryAddressService {

    private final DeliveryAddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressResDto addAddress(AddressReqDto addressReqDto, PrincipalDetails principalDetails) {

        User user = userRepository.findByUsernameAndDeletedAtIsNull(principalDetails.getUsername())
                .orElseThrow(()-> new IllegalArgumentException("Invalid username : " + principalDetails.getUsername()));

        // 동일한 user를 가지고있는 deliveryAddress 중에 중복된 명이 있으면 중복 반환
        if(addressRepository.existsByUserAndDeliveryAddress(user, addressReqDto.getDeliveryAddress())){
            throw new IllegalArgumentException("해당 유저의 동일한 배송지가 이미 존재합니다. :" + addressReqDto.getDeliveryAddress());
        }

        // user는 가지고있는 deliveryAddress수가 3개 까지만 가질 수 있음
        if (addressRepository.countByUser(user) >= 3){
            throw new IllegalArgumentException("배송지는 최대 3개 까지만 추가할 수 있습니다.");
        }

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .deliveryAddress(addressReqDto.getDeliveryAddress())
                .deliveryAddressInfo(addressReqDto.getDeliveryAddressInfo())
                .detailAddress(addressReqDto.getDetailAddress() != null ? addressReqDto.getDetailAddress() : "")
                .user(user)
                .build();

        user.addDeliveryAddress(deliveryAddress);

        userRepository.save(user);

        return deliveryAddress.toResponse();
    }

    @Transactional(readOnly = true)
    public AddressResDto getDeliveryAddressById(UUID id) {

        DeliveryAddress deliveryAddress = addressRepository.findByDeliveryAddressIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DeliveryAddressNotFoundException("DeliveryAddress Not Found By Id : "+ id));

        return  deliveryAddress.toResponse();
    }

    @Transactional(readOnly = true)
    public Page<AddressResDto> getDeliveryAddresses(AddressSearchDto addressSearchDto) {

        QDeliveryAddress qDeliveryAddress = QDeliveryAddress.deliveryAddress1;

        BooleanBuilder builder = buildSearchConditions(addressSearchDto,qDeliveryAddress);

        // 페이지네이션 설정
        Sort sort = getSortOrder(addressSearchDto);

        PageRequest pageRequest = PageRequest.of(addressSearchDto.getPage(), addressSearchDto.getSize(),sort);

        // 배송지 목록 조회 (페이징 + 검색 조건)
        Page<DeliveryAddress> deliveryAddressPages = addressRepository.findAll(builder,pageRequest);

        if (deliveryAddressPages.isEmpty()){
            throw new DeliveryAddressNotFoundException("deliveryAddress Not Found");
        }

        return deliveryAddressPages.map(DeliveryAddress :: toResponse);
    }

    private BooleanBuilder buildSearchConditions(AddressSearchDto addressSearchDto, QDeliveryAddress qDeliveryAddress) {

        BooleanBuilder builder = new BooleanBuilder();

        // username 조건
        if (addressSearchDto.getDeliveryAddress() != null && !addressSearchDto.getDeliveryAddress().isEmpty()){
            builder.and(qDeliveryAddress.deliveryAddress.containsIgnoreCase(addressSearchDto.getDeliveryAddress()));
        }

        // email 조건
        if (addressSearchDto.getDeliveryAddressInfo() != null && !addressSearchDto.getDeliveryAddressInfo().isEmpty()){
            builder.and(qDeliveryAddress.deliveryAddressInfo.containsIgnoreCase(addressSearchDto.getDeliveryAddressInfo()));
        }

        return builder;
    }

    private Sort getSortOrder(AddressSearchDto addressSearchDto) {

        String sortBy = addressSearchDto.getSortBy();

        if (!isValidSortBy(sortBy)) {
            throw new IllegalArgumentException("SortBy 는 'createdAt', 'updatedAt', 'deletedAt' 값만 허용합니다.");
        }

        Sort sort = Sort.by(Sort.Order.by(sortBy));

        sort = getSortDirection(sort, addressSearchDto.getOrder());

        return sort;
    }

    private boolean isValidSortBy(String sortBy) {
        return "createdAt".equals(sortBy) || "updatedAt".equals(sortBy) || "deletedAt".equals(sortBy);
    }

    private Sort getSortDirection(Sort sort, String order) {
        if (order.equals("desc")){
            return sort.descending();
        }else{
            return sort.ascending();
        }
    }
}
