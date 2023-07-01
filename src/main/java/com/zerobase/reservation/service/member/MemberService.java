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

    /**
     * 회원 가입
     */
    @Transactional
    public MemberDto signUp(String email, String nickname, String password, String phoneNumber, Role role) {

        //이메일과 닉네임이 중복되는지 확인
        duplicateCheckBy(email, nickname);

        return MemberDto.of(memberRepository.save(build(email, nickname, password, phoneNumber, role)));
    }

    /**
     * 회원 수정
     */
    @Transactional
    public MemberDto update(String email, String password, String nickname, String imageUrl, String phoneNumber) {

        Member member = getMemberBy(email);

        //변경하려는 닉네임이 기존 닉네임과 다를 때 닉네임이 이미 존재하는지 확인
        duplicateCheck(nickname, member.getNickname());

        //oauth2 회원가입 한 회원이 추가정보를 입력한 경우에 권한을 guest -> user 로 변경
        RoleGuestUpdateRoleUser(member);

        member.updateMember((passwordEncoder.encode(password)), nickname, imageUrl, phoneNumber);

        return MemberDto.of(member);
    }


    /**
     * 회원 탈퇴
     */
    @Transactional
    public void delete(String email, String password) {
        Member member = getMemberBy(email);

        //비밀번호가 일치하는지 확인
        validatePassword(password, member.getPassword());

        //회원을 삭제하기 전 자식테이블에 있는 회원기록들을 먼저 삭제
        deleteChildTables(member);

        memberRepository.delete(member);
    }

    /**
     * 회원 단건 조회
     */
    public MemberDto getMember(String email) {
        return MemberDto.of(getMemberBy(email));
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
