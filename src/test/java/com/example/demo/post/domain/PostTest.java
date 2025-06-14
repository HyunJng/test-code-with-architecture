package com.example.demo.post.domain;

import com.example.demo.mock.TestClockHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    void PostCreate으로_게시글을_만들_수_있다() throws Exception {
        //given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("helloworld")
                .build();

        User writer = User.builder()
                .email("kok202@naver.com")
                .nickname("kok202")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaaa-aaaaaaa-aaaaa-aaaaaa")
                .build();

        //when
        Post post = Post.from(postCreate, writer, new TestClockHolder(1000L));

        //then
        assertThat(post.getContent()).isEqualTo("helloworld");
        assertThat(post.getCreatedAt()).isEqualTo(1000L);
        assertThat(post.getWriter().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("kok202");
        assertThat(post.getWriter().getAddress()).isEqualTo("Seoul");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void PostUpdate으로_게시글을_수정할_수_있다() throws Exception {
        //given
        Post post = Post.builder()
                .id(1L)
                .content("helloworld")
                .writer(User.builder()
                        .email("kok202@naver.com")
                        .nickname("kok202")
                        .address("Seoul")
                        .status(UserStatus.ACTIVE)
                        .certificationCode("aaaaaa-aaaaa-aaaaaaa")
                        .build())
                .createdAt(1000L)
                .build();

        PostUpdate postUpdate = PostUpdate.builder()
                .content("new helloworld")
                .build();

        //when
        Post result = post.update(postUpdate, new TestClockHolder(1000L));

        //then
        assertThat(result.getContent()).isEqualTo("new helloworld");
        assertThat(result.getCreatedAt()).isEqualTo(1000L);
        assertThat(result.getWriter().getEmail()).isEqualTo("kok202@naver.com");
        assertThat(result.getWriter().getNickname()).isEqualTo("kok202");
        assertThat(result.getWriter().getAddress()).isEqualTo("Seoul");
        assertThat(result.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}