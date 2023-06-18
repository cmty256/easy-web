package com.chenmeng.project.controller;

import com.chenmeng.project.common.BaseResponse;
import com.chenmeng.project.model.dto.user.UserRegisterRequest;
import com.chenmeng.project.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户控制器
 *
 * @author 沉梦听雨
 * @date 2023/06/17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;



}
