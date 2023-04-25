package com.Saojung.whatisthis.controller;

import com.Saojung.whatisthis.dto.MemberDto;
import com.Saojung.whatisthis.service.MemberService;
import com.Saojung.whatisthis.vo.LoginVo;
import com.Saojung.whatisthis.vo.MemberVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    ObjectMapper objectMapper;

    @Mock
    MemberService memberService;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        JacksonTester.initFields(this, objectMapper);
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.mvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("회원가입 완료")
    void 회원가입_완료() throws Exception {
        //given
        MemberDto memberDto = new MemberDto(
                null, "castlehi", "password", "박성하", LocalDate.of(2000, 06, 17), "p_password", null, null
        );
        MemberDto resultDto = new MemberDto(
                1L, "castlehi", passwordEncoder.encode("password"), "박성하", LocalDate.of(2000, 06, 17), passwordEncoder.encode("p_password"), null, null
        );

        MemberVo memberVo = new MemberVo(
                null, "castlehi", "password", "박성하", LocalDate.of(2000, 06, 17), "p_password"
        );

        BDDMockito.given(memberService.signUp(memberDto))
                .willReturn(resultDto);

        //when
        //then
        mvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberVo)))
                .andExpect(status().isOk())
                .andExpect(content().string(resultDto.getName() + "님의 회원가입이 완료되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인")
    void 로그인() throws Exception {
        //given
        MemberDto memberDto = new MemberDto(
                null, "castlehi", "password", "박성하", LocalDate.of(2000, 06, 17), "p_password", null, null
        );

        MemberDto resultDto = new MemberDto(
                1L, "castlehi", passwordEncoder.encode("password"), "박성하", LocalDate.of(2000, 06, 17), passwordEncoder.encode("p_password"), null, null
        );

        BDDMockito.given(memberService.signUp(memberDto))
                .willReturn(resultDto);

        LoginVo loginVo = new LoginVo(
                "castlehi", "password", null
        );

        BDDMockito.given(memberService.login(loginVo))
                .willReturn(resultDto);

        LinkedMultiValueMap<String, String> login_info = new LinkedMultiValueMap<>();
        login_info.add("idx", String.valueOf(resultDto.getIdx()));
        login_info.add("id", String.valueOf(resultDto.getUserId()));
        login_info.add("password", String.valueOf(resultDto.getPassword()));
        login_info.add("name", String.valueOf(resultDto.getName()));
        login_info.add("birth", String.valueOf(resultDto.getBirth()));
        login_info.add("parentPassword", resultDto.getParentPassword());

        //when
        //then
        mvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginVo)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.valueOf(login_info)))
                .andDo(print());
    }
}