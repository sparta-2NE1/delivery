package com.sparta.delivery.domain.user.controller;

import com.sparta.delivery.domain.user.dto.UserReqDto;
import com.sparta.delivery.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserReqDto userReqDto){

        return  ResponseEntity.status(HttpStatus.OK)
                .body(userService.signup(userReqDto));
    }
}
