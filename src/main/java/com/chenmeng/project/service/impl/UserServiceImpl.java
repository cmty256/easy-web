package com.chenmeng.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenmeng.project.common.enums.ErrorCode;
import com.chenmeng.project.exception.BusinessException;
import com.chenmeng.project.model.dto.user.UserQueryRequest;
import com.chenmeng.project.model.entity.User;
import com.chenmeng.project.model.vo.UserVO;
import com.chenmeng.project.service.UserService;
import com.chenmeng.project.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

import static com.chenmeng.project.constant.UserConstant.ADMIN_ROLE;
import static com.chenmeng.project.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author chenmeng
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-06-17 21:07:31
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "chenmeng";

    @Override
    public long userRegister(String userAccount, String email, String phone, String userPassword, String confirmPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, email, phone, userPassword, confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || confirmPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 校验手机号码 -- 手机号码为 11 位数字，以 1 开头
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号码格式不正确");
        }
        // 校验邮箱 -- xxx@qq.com
        // 邮箱格式为 用户名@域名 的形式，域名必须包含一个"."，且"."后面必须跟着两个或三个字母，如".com"、".cn"等。
        if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式不正确");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getUserAccount, userAccount);
            long count = userMapper.selectCount(lambdaQueryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入新用户数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setEmail(email);
            user.setPhone(phone);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            // 4. 注册成功，返回用户id
            return user.getId();
        }
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 查询用户是否存在
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUserAccount, userAccount);
        lambdaQueryWrapper.eq(User::getUserPassword, userPassword);
        User user = userMapper.selectOne(lambdaQueryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 4. 记录用户的登录态到 session
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 1. 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 2. 从数据库查询
        // todo 追求性能的话可以注释，直接走缓存
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request 请求
     * @return boolean
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        // 1.1 从session中获取当前登录的用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        // 1.2 转成user类型
        User user = (User) userObj;
        // 判断user不能为空，且为管理员
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    @Override
    public long passwordBack(String userAccount, String email, String newPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, email, newPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (newPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和确认密码必须相同
        if (!newPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2. 根据用户账号和邮箱查询用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_account", userAccount).eq("email", email);
        User user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或邮箱不正确");
        }
        synchronized (userAccount.intern()) {
            // 3. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes());
            // 4. 更新数据
            user.setUserPassword(encryptPassword);
            boolean result = this.updateById(user);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "密码找回失败，数据库错误");
            }
            // 5. 返回用户id
            return user.getId();
        }
    }

    @Override
    public List<UserVO> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        // 构造查询条件
        User userQuery = new User();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        // 构造Lambda查询条件
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 执行查询操作
        List<User> userList = this.list(lambdaQueryWrapper);
        // 将查询到的User对象转化为UserVO对象
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return userVOList;
    }

    @Override
    public Page<UserVO> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
            long current = 1; // 默认当前页为第一页
            long size = 10; // 默认每页查询10条数据

            User userQuery = new User(); // 创建用户查询对象
            if (userQueryRequest != null) { // 如果查询请求参数不为空
                BeanUtils.copyProperties(userQueryRequest, userQuery); // 将请求参数拷贝到用户查询对象中
                current = userQueryRequest.getCurrent(); // 获取当前页数
                size = userQueryRequest.getPageSize(); // 获取每页查询记录数
            }

            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>(); // 创建Lambda查询条件对象
            Page<User> userPage = this.page(new Page<>(current, size), lambdaQueryWrapper); // 分页查询用户信息
            Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal()); // 创建用户VO分页对象

            List<UserVO> userVOList = userPage.getRecords().stream().map(user -> {
                UserVO userVO = new UserVO(); // 创建用户VO对象
                BeanUtils.copyProperties(user, userVO); // 将用户信息拷贝到用户VO对象中
                return userVO;
            }).collect(Collectors.toList()); // 将用户VO对象转化为List集合

            // 将userVOList列表中的元素设置到userVOPage的记录（recodes）属性中
            userVOPage.setRecords(userVOList);
            return userVOPage;
    }

}




