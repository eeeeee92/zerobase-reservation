package com.zerobase.reservation.global.security.hadnler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.dto.member.MemberDto;
import com.zerobase.reservation.dto.oauth2.CustomOAuth2User;
import com.zerobase.reservation.global.security.jwt.JwtService;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.type.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler, JsonResponseHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        if (oAuth2User.getRole() == Role.GUEST) {
            String accessJwtToken = jwtService.createAccessToken(oAuth2User.getEmail());
            jwtService.sendAccessToken(response, accessJwtToken);
            //TODO
            response.sendRedirect("https://www.naver.com");// 프론트단의 추가정보 입력 페이지



        } else {
            loginSuccess(response, oAuth2User);
        }

    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        MemberDto memberDto = jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);

        writeResponse(response, objectMapper, memberDto);

    }
}
