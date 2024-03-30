package com.chenmeng.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenmeng.project.model.dto.user.UserQueryRequest;
import com.chenmeng.project.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenmeng.project.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author chenmeng
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-06-17 21:07:31
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param confirmPassword 确认密码
     * @param email         邮箱
     * @param phone         电话
     * @return 新用户 id
     */
    long userRegister(String userAccount, String email, String phone, String userPassword, String confirmPassword);


    /**
     * 用户登录
     *
     * @param userAccount  用户帐户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return 退出登录成功返回 true
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 查询用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @param request          请求
     * @return {@code List<UserVO>}
     */
    List<UserVO> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request);

    /**
     * 分页查询用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @param request          请求
     * @return {@code Page<UserVO>}
     */
    Page<UserVO> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request);

    /**
     * 用户密码找回
     *
     * @param userAccount     用户帐户
     * @param email           电子邮件
     * @param userPassword    用户密码
     * @param confirmPassword 确认密码
     * @return long
     */
    long passwordBack(String userAccount, String email, String userPassword, String confirmPassword);
}
