package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CertificationServiceTest {

    @Test
    void 이메일과_컨텐츠가_제대로_만들어져서_전송되는지_테스트한다() throws Exception {
        //given
        FakeMailSender fakeMailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(fakeMailSender);
        String email = "test101@naver.com";
        String certificationCode = "aaaaa-aaaaa-aaaaa";
        long id = 1;
        //when
        certificationService.send(email, id, certificationCode);

        //then
        assertThat(fakeMailSender.email).isEqualTo(email);
        assertThat(fakeMailSender.title).isEqualTo("Please certify your email address");
        assertThat(fakeMailSender.content).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aaaaa-aaaaa-aaaaa");
    }
}