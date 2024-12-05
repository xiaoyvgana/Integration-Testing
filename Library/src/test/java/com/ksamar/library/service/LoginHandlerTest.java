package com.ksamar.library.service;

import com.alibaba.fastjson.JSONObject;
import com.ksamar.library.controller.LoginHandler;
import com.ksamar.library.entitys.User;
import com.ksamar.library.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class LoginHandlerTest {

    @Autowired
    private LoginHandler loginHandler;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testLoginSuccess() {
        // 创建一个具有正确用户名和密码的用户对象
        User testUser = new User();
        testUser.setUsername("张三");
        testUser.setPassword("123456");

        // 假设你的 findOne 方法返回的 User 对象是 testUser
        when(userRepository.findOne(any())).thenReturn(Optional.of(testUser));

        // 当密码匹配时返回 true
        when(passwordEncoder.matches(eq("123456"), anyString())).thenReturn(true);  // 这里是正确的密码和编码匹配

        // 调用登录处理程序，传入正确的用户名和密码
        JSONObject response = loginHandler.login(testUser, "张三");

        // 校验返回的状态码和消息
        assertEquals(1, response.getInteger("statusCode"));
        assertEquals("登录成功", response.getString("message"));
    }


    @Test
    public void testLoginUserNotFound() {

        User testUser = new User();
        testUser.setUsername("user");
        testUser.setPassword("123456");

        // 模拟未找到用户的情况，返回空 Optional
        when(userRepository.findOne(any())).thenReturn(Optional.empty());

        // 调用登录方法，传入用户名 "user"
        JSONObject response = loginHandler.login(testUser, "user");

        // 校验返回的状态码和消息
        assertEquals(0, response.getInteger("statusCode"));
        assertEquals("用户名不存在", response.getString("message"));
    }

    @Test
    public void testLoginPasswordIncorrect() {

        User testUser = new User();
        testUser.setUsername("张三");
        testUser.setPassword("123");
        // 模拟找到了用户，但密码不匹配的情况
        when(userRepository.findOne(any())).thenReturn(Optional.of(testUser));

        // 模拟密码不匹配，传入错误密码
        when(passwordEncoder.matches(eq("wrongpassword"), anyString())).thenReturn(false);

        // 调用登录方法，传入用户名 "user"
        JSONObject response = loginHandler.login(testUser, "user");

        // 校验返回的状态码和消息
        assertEquals(0, response.getInteger("statusCode"));
        assertEquals("密码错误", response.getString("message"));
    }

}


