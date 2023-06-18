package com.zerobase.reservation.controller.member;

import com.zerobase.reservation.dto.member.SignupDto;
import com.zerobase.reservation.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public void signUp(@RequestBody SignupDto signupDto) {
        memberService.signUp(
                signupDto.getEmail(),
                signupDto.getNickname(),
                signupDto.getPassword(),
                signupDto.getPhoneNumber(),
                signupDto.getRole());
    }
}
