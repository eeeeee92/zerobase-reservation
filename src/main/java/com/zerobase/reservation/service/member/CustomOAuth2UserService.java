package com.zerobase.reservation.service.member;

import com.zerobase.reservation.domain.Member;
import com.zerobase.reservation.dto.oauth2.CustomOAuth2User;
import com.zerobase.reservation.dto.oauth2.OAuthAttributes;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.type.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);
        Member createMember = getMember(extractAttributes, socialType);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createMember.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createMember.getEmail(),
                createMember.getRole()
        ) ;
    }

    private SocialType getSocialType(String registrationId) {
        switch (registrationId){
            case NAVER: return SocialType.NAVER;
            case KAKAO: return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

    private Member getMember(OAuthAttributes attributes, SocialType socialType){
        Member member = memberRepository.findBySocialTypeAndSocialId(socialType, attributes.getOauth2UserInfo().getId())
                .orElse(null);
        if(member == null){
            return saveMember(attributes, socialType);
        }
        return member;
    }

    private Member saveMember(OAuthAttributes attributes, SocialType socialType){
        Member member = attributes.toEntity(socialType, attributes.getOauth2UserInfo());
        return memberRepository.save(member);
    }
}
