package com.chenmeng.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenmeng.project.model.entity.User;
import com.chenmeng.project.service.UserService;
import com.chenmeng.project.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 沉梦听雨
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-06-17 21:07:31
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




