package com.example.demo.service;

import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@SqlGroup(value = {
        @Sql(value = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private JavaMailSender javaMailSender;

    @Test
    void getByEmail_은_ACTIVE_상태인_유저를_찾아올_수_있다() throws Exception {
        //given
        String email = "test101@naver.com";

        //when
        UserEntity result = userService.getByEmail(email);

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
        UserEntity result = userService.getById(id);

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
    void userCreateDto_를_이용하여_유저를_생성함() throws Exception {
        //given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("test303@naver.com")
                .address("Gyeongi")
                .nickname("test303")
                .build();
        BDDMockito.doNothing().when(javaMailSender).send(SimpleMailMessage.class.newInstance());

        //when
        UserEntity result = userService.create(userCreateDto);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
//        assertThat(result.getCertificationCode()).isEqualTo(""); //FIXME
    }

    @Test
    void userUpdateDto_를_이용하여_유저를_수정함() throws Exception {
        //given
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .address("Incheon")
                .nickname("test202-n")
                .build();

        //when
        userService.update(1, userUpdateDto);

        //then
        UserEntity result = userService.getById(1);
        assertThat(result.getAddress()).isEqualTo(userUpdateDto.getAddress());
        assertThat(result.getNickname()).isEqualTo(userUpdateDto.getNickname());
    }

    @Test
    void user를_로그인_시키면_마지막_로그인_시간이_변경된다() throws Exception {
        //given
        //when
        userService.login(1);

        //then
        UserEntity result = userService.getById(1);
        assertThat(result.getLastLoginAt()).isGreaterThan(0L);
//        assertThat(result.getLastLoginAt()).isEqualTo("..."); //FIXME
    }

    @Test
    void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다() throws Exception {
        //given
        //when
        userService.verifyEmail(2, "bbbbb-bbbbb-bbbbb");

        //then
        UserEntity result = userService.getById(2);
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