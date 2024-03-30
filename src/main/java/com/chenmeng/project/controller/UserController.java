package com.chenmeng.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenmeng.project.common.BaseResponse;
import com.chenmeng.project.common.DeleteRequest;
import com.chenmeng.project.common.enums.ErrorCode;
import com.chenmeng.project.common.utils.ResultUtils;
import com.chenmeng.project.exception.BusinessException;
import com.chenmeng.project.model.dto.user.*;
import com.chenmeng.project.model.entity.User;
import com.chenmeng.project.model.vo.UserVO;
import com.chenmeng.project.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户控制器
 *
 * @author chenmeng
 * @date 2023/06/17
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String email = userRegisterRequest.getEmail();
        String phone = userRegisterRequest.getPhone();
        String userPassword = userRegisterRequest.getUserPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        if (StringUtils.isAnyBlank(userAccount, email, phone, userPassword, confirmPassword)) {
            return null;
        }

        long result = userService.userRegister(userAccount, email, phone, userPassword, confirmPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }


    /**
     * 用户注销
     *
     * @param request 请求
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }


    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return {@code BaseResponse<UserVO>} 用户视图
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 用户密码找回
     *
     * @param userPasswBackRequest 用户密码找回请求体
     * @return {@code BaseResponse<Long>}
     */// endregion
    @PostMapping("/passwordBack")
    public BaseResponse<Long> passwordBack(@RequestBody UserPasswBackRequest userPasswBackRequest) {
        if (userPasswBackRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userPasswBackRequest.getUserAccount();
        String email = userPasswBackRequest.getEmail();
        String userPassword = userPasswBackRequest.getNewPassword();
        String confirmPassword = userPasswBackRequest.getConfirmPassword();
        if (StringUtils.isAnyBlank(userAccount, email, userPassword, confirmPassword)) {
            return null;
        }
        long result = userService.passwordBack(userAccount, email, userPassword, confirmPassword);
        return ResultUtils.success(result);
    }

    // endregion

    // region 增删改查


    /**
     * 添加用户
     *
     * @param userAddRequest 用户添加请求
     * @param request        请求
     * @return {@code BaseResponse<Long>}
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @param request           请求
     * @return {@code BaseResponse<Boolean>}
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id      id
     * @param request 请求
     * @return {@code BaseResponse<UserVO>}
     */
    @GetMapping("/get")
    public BaseResponse<UserVO> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 查询用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    public BaseResponse<List<UserVO>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        List<UserVO> userVOList = userService.listUser(userQueryRequest, request);
        // 返回结果
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页查询用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @param request          请求
     * @return {@code BaseResponse<Page<UserVO>>}
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        Page<UserVO> userVOPage = userService.listUserByPage(userQueryRequest, request);
        return ResultUtils.success(userVOPage); // 返回用户VO分页对象
    }

    // endregion


}
