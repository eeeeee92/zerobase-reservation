package com.zerobase.reservation.service.member;

import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.dto.member.MemberDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.zerobase.reservation.global.exception.ErrorCode.ALREADY_EXIST_EMAIL;
import static com.zerobase.reservation.global.exception.ErrorCode.ALREADY_EXIST_NICKNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
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
        //when
        MemberDto memberDto = memberService.signUp(email, nickname, password, phoneNumber, role);

        //then
        assertThat(memberDto).extracting("email", "nickname", "phoneNumber", "role")
                .contains(email, nickname, phoneNumber, role);
        assertNotEquals(password, memberDto.getPassword());
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
        String encodingPassword  = "암호화";
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
}