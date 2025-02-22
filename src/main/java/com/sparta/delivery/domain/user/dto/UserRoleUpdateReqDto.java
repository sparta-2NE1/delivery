package com.sparta.delivery.domain.user.dto;

import com.sparta.delivery.domain.user.enums.UserRoles;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleUpdateReqDto {

    @NotNull(message = "Role 값은 필수입니다.")
    private UserRoles role;
}
