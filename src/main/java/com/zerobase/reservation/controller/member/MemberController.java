package com.zerobase.reservation.controller.member;

import com.zerobase.reservation.dto.member.DeleteMemberDto;
import com.zerobase.reservation.dto.member.MemberInfoDetail;
import com.zerobase.reservation.dto.member.SignupDto;
import com.zerobase.reservation.dto.member.UpdateMemberDto;
import com.zerobase.reservation.global.annotation.Trace;
import com.zerobase.reservation.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입
     */
    @Trace
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignupDto signupDto) {
        memberService.signUp(
                signupDto.getEmail(),
                signupDto.getNickname(),
                signupDto.getPassword(),
                signupDto.getPhoneNumber(),
                signupDto.getRole());
        return ResponseEntity.ok().build();
    }

    /**
     * 회원수정
     */
    @Trace
    @PutMapping("/{email}")
    @PreAuthorize("isAuthenticated() and ((#email == principal.username))")
    public ResponseEntity<?> update(@PathVariable String email,
                                    @Valid @RequestBody UpdateMemberDto.Request updateMemberDto) {

        memberService.update(
                email,
                updateMemberDto.getPassword(),
                updateMemberDto.getNickname(),
                updateMemberDto.getImageUrl(),
                updateMemberDto.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴
     */
    @Trace
    @DeleteMapping("/{email}")
    @PreAuthorize("isAuthenticated() and ((#email == principal.username))")
    public ResponseEntity<?> delete(@PathVariable String email,
                                    @Valid @RequestBody DeleteMemberDto.Request deleteMemberDto) {

        memberService.delete(email, deleteMemberDto.getPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 단건 조회
     */
    @Trace
    @GetMapping("/{email}")
    @PreAuthorize("isAuthenticated() and ((#email == principal.username))")
    public ResponseEntity<MemberInfoDetail.Response> read(@PathVariable String email) {
        return ResponseEntity.ok(
                MemberInfoDetail.Response.of(memberService.getMember(email))
        );
    }

    //TODO 회원 전체조회 (검색 조건 별)
}
