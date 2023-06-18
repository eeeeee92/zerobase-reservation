package com.zerobase.reservation.global.security.hadnler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerobase.reservation.dto.member.LoginDto;
import com.zerobase.reservation.dto.member.MemberDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface JsonResponseHandler {
    default void writeResponse(HttpServletResponse response, ObjectMapper objectMapper, MemberDto memberDto) {

        LoginDto.Response responseDto = LoginDto.Response.builder()
                .email(memberDto.getEmail())
                .nickname(memberDto.getNickname())
                .imageUrl(memberDto.getImageUrl())
                .build();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(responseDto);
            response.setCharacterEncoding("UTF-8");
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(json);
        } catch (JsonProcessingException e) {
            //TODO json 파싱 문제 인터널 서버에러 처리
            throw new RuntimeException(e);
        } catch (IOException e) {
            //TODO response 문제 인터널 서버에러 처리
            throw new RuntimeException(e);
        }

    }
}
