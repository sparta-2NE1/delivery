package com.sparta.delivery.domain.delivery_address.service;

import com.sparta.delivery.config.auth.PrincipalDetails;
import com.sparta.delivery.domain.delivery_address.dto.AddressReqDto;
import com.sparta.delivery.domain.delivery_address.dto.AddressResDto;
import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.delivery_address.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressResDto addAddress(AddressReqDto addressReqDto, PrincipalDetails principalDetails) {

        User user = userRepository.findByUsernameAndDeletedAtIsNull(principalDetails.getUsername())
                .orElseThrow(()-> new IllegalArgumentException("Invalid username : " + principalDetails.getUsername()));

        //동일한 user를 가지고있는 deliveryAddress 중에 중복된 네임이 잇으면 중복 반환
        if(addressRepository.existsByUserAndDeliveryAddress(user, addressReqDto.getDeliveryAddress())){
            throw new IllegalArgumentException("해당 유저의 동일한 배송지가 이미 존재합니다. :" + addressReqDto.getDeliveryAddress());
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
}
