package com.zerobase.reservation.service.member;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.dto.member.MemberDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.review.ReviewRepository;
import com.zerobase.reservation.repository.shop.MemberShopRepository;
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
    private final MemberShopRepository memberShopRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public MemberDto signUp(String email, String nickname, String password, String phoneNumber, Role role) {

        duplicateCheckBy(email, nickname);

        Member saveMember = memberRepository.save(build(email, nickname, password, phoneNumber, role));
        return MemberDto.of(saveMember);
    }


    @Transactional
    public MemberDto update(String email, String password, String nickname, String imageUrl, String phoneNumber) {

        Member member = getMemberBy(email);

        duplicateCheck(nickname, member.getNickname());

        RoleGuestUpdateRoleUser(member);

        member.updateMember((passwordEncoder.encode(password)), nickname, imageUrl, phoneNumber);

        return MemberDto.of(member);
    }


    @Transactional
    public void delete(String email, String password) {
        Member member = getMemberBy(email);

        validatePassword(password, member.getPassword());

        deleteChildTables(member);

        memberRepository.delete(member);
    }

    private void deleteChildTables(Member member) {
        memberShopRepository.deleteByMemberId(member.getId());
        reviewRepository.deleteByMemberId(member.getId());
        reservationRepository.deleteByMemberId(member.getId());
    }


    private void duplicateCheckBy(String email, String nickname) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new ArgumentException(ALREADY_EXIST_EMAIL, email);
        }

        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new ArgumentException(ALREADY_EXIST_NICKNAME, nickname);
        }
    }

    private void duplicateCheck(String nickname, String memberNickname) {
        if (memberRepository.findByNickname(nickname).isPresent() && !memberNickname.equals(nickname)) {
            throw new ArgumentException(ALREADY_EXIST_NICKNAME, nickname);
        }
    }

    private Member getMemberBy(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ArgumentException(ErrorCode.MEMBER_NOT_FOUND, email));
    }


    private void validatePassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new ArgumentException(ErrorCode.UN_MATCH_PASSWORD);
        }
    }

    private static void RoleGuestUpdateRoleUser(Member member) {
        if (member.getRole() == Role.GUEST) {
            member.authorizeUser();
        }
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
