package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-08-28
 */
public interface UserInfoService extends IService<UserInfo> {

    Map login(LoginVo loginVo);

    UserInfo getUserInfo(Long userId);

    Page<UserInfo> getPageUserInfo(Integer pageNum, Integer limit, UserInfoQueryVo userInfoQueryVo);

    void updateStatus(Long id, Integer authStatus);

    Map<String, Object> detail(Long id);

    void updateAuthStatus(Long id, Integer authStatus);
}
