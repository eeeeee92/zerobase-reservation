package com.zerobase.reservation.service.member;

import com.zerobase.reservation.domain.Member;
import com.zerobase.reservation.repository.MemberRepository;
import com.zerobase.reservation.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(String email, String nickname, String password, String phoneNumber, Role role){
        if(memberRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("이미 존재하느 이메일" + email);
        }

        if(memberRepository.findByNickname(nickname).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 닉네임");
        }

        memberRepository.save(
                build(email, nickname, password, phoneNumber, role)
        );

    }

    private Member build(String email, String nickname, String password, String phoneNumber, Role role) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();
    }
}
