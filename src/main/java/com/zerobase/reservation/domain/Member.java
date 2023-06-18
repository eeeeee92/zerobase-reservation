package com.zerobase.reservation.domain;

import com.zerobase.reservation.type.Role;
import com.zerobase.reservation.type.SocialType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private String imageUrl;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Builder
    private Member(String email, String password, String nickname, String imageUrl, String phoneNumber, Role role, SocialType socialType, String socialId, String refreshToken) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
    }

    private String socialId;

    private String refreshToken;

    public void authorizeUser() {
        this.role = Role.USER;
    }

    public void updateRefreshToken(String updateRefreshToken){
        this.refreshToken = updateRefreshToken;
    }
}
