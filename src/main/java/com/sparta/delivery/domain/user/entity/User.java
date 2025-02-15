package com.sparta.delivery.domain.user.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.user.dto.UserResDto;
import com.sparta.delivery.domain.user.enums.UserRoles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_user")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String email;

    @Column
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRoles role;

    public UserResDto toResponseDto() {

        return new UserResDto(
                this.userId,
                this.email,
                this.username,
                this.nickname,
                this.role.name()
        );
    }
}
