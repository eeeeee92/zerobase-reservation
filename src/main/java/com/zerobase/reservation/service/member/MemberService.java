package com.zerobase.reservation.service.member;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.dto.member.MemberDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.zerobase.reservation.global.exception.ErrorCode.ALREADY_EXIST_EMAIL;
import static com.zerobase.reservation.global.exception.ErrorCode.ALREADY_EXIST_NICKNAME;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberDto signUp(String email, String nickname, String password, String phoneNumber, Role role) {

        if (memberRepository.findByEmail(email).isPresent()) {
            throw new ArgumentException(ALREADY_EXIST_EMAIL, email);
        }

        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new ArgumentException(ALREADY_EXIST_NICKNAME, nickname);
        }

        Member saveMember = memberRepository.save(build(email, nickname, password, phoneNumber, role));
        return MemberDto.of(saveMember);
    }

    @Transactional
    public MemberDto update(String email, String password, String nickname, String imageUrl, String phoneNumber) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ArgumentException(ErrorCode.MEMBER_NOT_FOUND, email));

        if (memberRepository.findByNickname(nickname).isPresent() && !member.getNickname().equals(nickname)) {
            throw new ArgumentException(ALREADY_EXIST_NICKNAME, nickname);
        }

        if(member.getRole() == Role.GUEST){
            member.authorizeUser();
        }

        member.updateMember(passwordEncoder.encode(password), nickname, imageUrl, phoneNumber);

        return MemberDto.of(member);
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
