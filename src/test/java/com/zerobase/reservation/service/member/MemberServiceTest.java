package com.zerobase.reservation.service.member;

import com.zerobase.reservation.config.AcceptanceTest;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.dto.member.MemberDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.zerobase.reservation.global.exception.ErrorCode.ALREADY_EXIST_EMAIL;
import static com.zerobase.reservation.global.exception.ErrorCode.ALREADY_EXIST_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@AcceptanceTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입")
    public void singUp() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String nickname = "제로베이스";
        String password = "123";
        String phoneNumber = "01000000000";
        Role role = Role.USER;
        given(memberRepository.save(any())).willReturn(
                getMemberEntity(email, nickname, phoneNumber, role));

        //when
        MemberDto memberDto = memberService.signUp(email, nickname, password, phoneNumber, role);

        //then
        assertThat(memberDto).extracting("email", "nickname", "phoneNumber", "role")
                .contains(email, nickname, phoneNumber, role);
    }


    @Test
    @DisplayName("회원가입시 이메일이 이미 존재한다면 예외가 발생한다")
    public void signUp_already_exist_email() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String nickname = "제로베이스";
        String password = "123";
        String phoneNumber = "01000000000";
        Role role = Role.USER;
        Member member = Member.builder()
                .email(email)
                .build();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        ArgumentException argumentException = new ArgumentException(ALREADY_EXIST_EMAIL, email);

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> memberService.signUp(email, nickname, password, phoneNumber, role));

        assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());

    }

    @Test
    @DisplayName("회원가입시 닉네임이 이미 존재한다면 예외가 발생한다")
    public void signUp_already_exist_nickname() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String nickname = "제로베이스";
        String password = "123";
        String phoneNumber = "01000000000";
        Role role = Role.USER;
        Member member = Member.builder()
                .nickname(nickname)
                .build();
        given(memberRepository.findByNickname(nickname)).willReturn(Optional.of(member));
        ArgumentException argumentException = new ArgumentException(ALREADY_EXIST_NICKNAME, nickname);
        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> memberService.signUp(email, nickname, password, phoneNumber, role));

        assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("회원가입시 비밀번호는 암호화돼서 저장돼야 한다")
    public void signUp_password_encoding() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String nickname = "제로베이스";
        String password = "123";
        String phoneNumber = "01000000000";
        String encodingPassword = "암호화";
        Role role = Role.USER;
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .phoneNumber(phoneNumber)
                .password(password)
                .role(role)
                .build();
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        given(passwordEncoder.encode(any())).willReturn(encodingPassword);
        given(memberRepository.save(any())).willReturn(member);

        //when
        MemberDto memberDto = memberService.signUp(email, nickname, password, phoneNumber, role);

        //then
        verify(memberRepository, times(1)).save(captor.capture());
        verify(passwordEncoder, times(1)).encode(any());
        assertEquals(encodingPassword, captor.getValue().getPassword());
    }

    @Test
    @DisplayName("회원을 업데이트한다")
    public void update() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String nickname = "닉네임";
        String password = "password";
        String imageUrl = "imageUrl";
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .imageUrl(imageUrl)
                .phoneNumber("01000000000")
                .role(Role.GUEST)
                .build();
        String encodedPassword = "encodedPassword";
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);

        //when
        MemberDto memberDto = memberService.update(email, password, nickname, imageUrl, null);

        //then
        assertThat(memberDto).extracting("email", "password", "nickname", "imageUrl", "phoneNumber", "role")
                .contains(email, encodedPassword, nickname, imageUrl, null, Role.USER);
    }

    @Test
    @DisplayName("회원을 업데이트시 회원이 존재하지 않으면 예외가 발생한다")
    public void update_memberNotFound() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String nickname = "닉네임";
        String password = "password";
        String imageUrl = "imageUrl";


        given(memberRepository.findByEmail(email)).willReturn(Optional.empty());
        ArgumentException argumentException = new ArgumentException(ErrorCode.MEMBER_NOT_FOUND, email);

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> memberService.update(email, password, nickname, imageUrl, null));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());

    }

    @Test
    @DisplayName("닉네임 변경시 닉네임이 이미 존재하면 예외가 발생한다")
    public void update_alreadyExistNickname() throws Exception {
        //given
        String nickname = "닉네임";
        String updateNickname = "updateNickname";


        String email = "zerobase@naver.com";
        String password = "password";
        String imageUrl = "imageUrl";
        Member member = Member.builder()
                .nickname(nickname)
                .build();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(memberRepository.findByNickname(updateNickname)).willReturn(Optional.of(Member.builder().nickname(updateNickname).build()));
        ArgumentException argumentException = new ArgumentException(ALREADY_EXIST_NICKNAME, updateNickname);

        //when //then
        ArgumentException exception = assertThrows(ArgumentException.class, () -> memberService.update(email, password, updateNickname, imageUrl, null));

        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());

    }

    @Test
    @DisplayName("회원 탈퇴")
    public void delete() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String password = "password";
        Member member = Member.builder()
                .password(password)
                .build();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(password, member.getPassword())).willReturn(true);
        //when
        memberService.delete(email, password);

        //then
        verify(passwordEncoder, times(1)).matches(password, member.getPassword());
        verify(memberRepository, times(1)).delete(member);
    }

    @Test
    @DisplayName("회원 탈퇴시 비밀번호가 일치하지 않을 시 예외가 발생한다")
    public void delete_unMatchPassword() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String memberPassword = "password";
        String requestPassword = "request";
        Member member = Member.builder()
                .password(memberPassword)
                .build();
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(passwordEncoder.matches(requestPassword, memberPassword)).willReturn(false);
        ArgumentException argumentException = new ArgumentException(ErrorCode.UN_MATCH_PASSWORD);

        //when
        ArgumentException exception = assertThrows(ArgumentException.class, () -> memberService.delete(email, requestPassword));

        //then
        assertThat(exception).extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }


    //TODO 회원 단건 조회 테스트

    private static Member getMemberEntity(String email, String nickname, String phoneNumber, Role role) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();
    }
}