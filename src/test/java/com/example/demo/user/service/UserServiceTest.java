package com.example.demo.user.service;

import com.example.demo.common.domain.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void init() {
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.userService = UserService.builder()
                .certificationService(new CertificationService(fakeMailSender))
                .userRepository(fakeUserRepository)
                .uuidHolder(new TestUuidHolder("aaaaa-aaaaaa-aaaaaa"))
                .clockHolder(new TestClockHolder(1000L))
                .build();
        fakeUserRepository.save(User.builder()
                .email("test101@naver.com")
                .nickname("test101")
                .status(UserStatus.ACTIVE)
                .address("Seoul")
                .certificationCode("aaaaa-aaaaa-aaaaa")
                .lastLoginAt(0L)
                .build());
        fakeUserRepository.save(User.builder()
                .email("test102@naver.com")
                .nickname("test102")
                .status(UserStatus.PENDING)
                .address("Seoul")
                .certificationCode("bbbbb-bbbbb-bbbbb")
                .lastLoginAt(0L)
                .build());
    }

    @Test
    void getByEmail_은_ACTIVE_상태인_유저를_찾아올_수_있다() throws Exception {
        //given
        String email = "test101@naver.com";

        //when
        User result = userService.getByEmail(email);

        //then
        assertThat(result.getNickname()).isEqualTo("test101");
    }

    @Test
    void getByEmail_은_PENDING_상태인_유저를_찾아올_수_없다() throws Exception {
        //given
        String email = "test102@naver.com";

        //when
        //then
        assertThatThrownBy(() -> userService.getByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getById_는_ACTIVE_상태인_유저를_찾아올_수_있다() throws Exception {
        //given
        long id = 1;

        //when
        User result = userService.getById(id);

        //then
        assertThat(result.getNickname()).isEqualTo("test101");
    }

    @Test
    void getById_는_PENDING_상태인_유저를_찾아올_수_없다() throws Exception {
        //given
        long id = 2;

        //when
        //then
        assertThatThrownBy(() -> userService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void userCreate_를_이용하여_유저를_생성함() throws Exception {
        //given
        UserCreate userCreate = UserCreate.builder()
                .email("test303@naver.com")
                .address("Gyeongi")
                .nickname("test303")
                .build();

        //when
        User result = userService.create(userCreate);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaa-aaaaaa-aaaaaa");
    }

    @Test
    void userUpdate_를_이용하여_유저를_수정함() throws Exception {
        //given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Incheon")
                .nickname("test202-n")
                .build();

        //when
        userService.update(1, userUpdate);

        //then
        User result = userService.getById(1);
        assertThat(result.getAddress()).isEqualTo(userUpdate.getAddress());
        assertThat(result.getNickname()).isEqualTo(userUpdate.getNickname());
    }

    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() throws Exception {
        //given
        //when
        userService.login(1);

        //then
        User result = userService.getById(1);
        assertThat(result.getLastLoginAt()).isGreaterThan(0L);
        assertThat(result.getLastLoginAt()).isEqualTo(1000L);
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다() throws Exception {
        //given
        //when
        userService.verifyEmail(2, "bbbbb-bbbbb-bbbbb");

        //then
        User result = userService.getById(2);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러를_던진다() throws Exception {
        //given
        //when
        //then
        assertThatThrownBy(() -> userService.verifyEmail(2, "wrong"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }




}