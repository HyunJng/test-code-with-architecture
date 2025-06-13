package com.example.demo.user.domain;

import com.example.demo.common.domain.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void UserCreate_객체로_생성할_수_있다() throws Exception {
        //given
        UserCreate userCreate = UserCreate.builder()
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .build();

        //when
        TestUuidHolder testUuidHolder = new TestUuidHolder("aaaaa-aaaa-aaaaaa");
        User user = User.from(userCreate, testUuidHolder);

        //then
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("kok202@naver.com");
        assertThat(user.getNickname()).isEqualTo("kok202");
        assertThat(user.getAddress()).isEqualTo("Seoul");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaa-aaaa-aaaaaa");
    }

    @Test
    void UserUpdate_객체로_데이터를_업데이트_할_수_있다() throws Exception {
        //given
        User user = User.builder()
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaaa-aaaaaa")
                .build();
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("koko333")
                .address("Busan")
                .build();

        //when
        User result = user.update(userUpdate);

        //then
        assertThat(result.getEmail()).isEqualTo("kok202@naver.com");
        assertThat(result.getNickname()).isEqualTo("koko333");
        assertThat(result.getAddress()).isEqualTo("Busan");
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getLastLoginAt()).isEqualTo(100L);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaa-aaaa-aaaaaa");
    }

    @Test
    void 로그인을_할_수_있고_로그인시_마지막_시간이_저장된다() throws Exception {
        //given
        User user = User.builder()
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaaa-aaaaaa")
                .build();
        TestClockHolder testClockHolder = new TestClockHolder(20250614);

        //when
        User result = user.login(testClockHolder);

        //then
        assertThat(result.getLastLoginAt()).isEqualTo(20250614);
    }

    @Test
    void 유효한_인증_코드로_계정을활성화_할_수_있다() throws Exception {
        //given
        User user = User.builder()
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaaa-aaaaaa")
                .build();

        //when
        user = user.certificate("aaaaa-aaaa-aaaaaa");

        //then
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void 잘못된_인증_코드로_계정을_활성화_하려하면_에러를_던진다() throws Exception {
        //given
        User user = User.builder()
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .certificationCode("aaaaa-aaaa-aaaaaa")
                .build();

        //when
        //then
        assertThatThrownBy(() -> user.certificate("bbbbb-bbbb-aaaaaa"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}