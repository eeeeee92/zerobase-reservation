package com.zerobase.reservation.domain.member;

import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.shop.MemberShop;
import com.zerobase.reservation.domain.shop.StarRating;
import com.zerobase.reservation.type.Role;
import com.zerobase.reservation.type.SocialType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberShop> memberShops = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StarRating> starRatings = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

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

    public void updateMember(String password, String nickname, String imageUrl, String phoneNumber) {
        this.password = password;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}
