package com.atguigu.yygh.user.service.impl;


import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.enums.StatusEnum;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-08-28
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PatientService patientService;
    @Override
    public Map login(LoginVo loginVo) {
        //1.获取输入的手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //2.非空判断
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new yyghException(20001,"手机号或验证码为空");
        }
        //3.判断验证码是否正确
        String redisCode = (String)redisTemplate.opsForValue().get(phone);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)){
            throw new yyghException(20001,"验证码错误");
        }
        String openid = loginVo.getOpenid();
        UserInfo userInfo = null;

        if (StringUtils.isEmpty(openid)){
            //4.判断用户是首次登入
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("phone",phone);
            userInfo = baseMapper.selectOne(queryWrapper);


            if (userInfo == null){
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                baseMapper.insert(userInfo);
                userInfo.setStatus(1);
            }
            //5.判断status状态
            if (userInfo.getStatus() == 0){
                throw new yyghException(20001,"用户被禁用");
            }
        }else {//微信登录 首次使用微信登入绑定手机号

            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("openid",openid);
            userInfo = baseMapper.selectOne(queryWrapper);

            QueryWrapper phoneWrapper = new QueryWrapper();
            phoneWrapper.eq("phone",phone);
            UserInfo userInfo2 = baseMapper.selectOne(phoneWrapper);

            if (userInfo2 == null){
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.updateById(userInfo);
            }else {
                userInfo.setPhone(userInfo2.getNickName());
                userInfo.setOpenid(userInfo2.getOpenid());
                baseMapper.updateById(userInfo);
                baseMapper.deleteById(userInfo2.getId());
            }
        }

        //6.返回登录信息
        Map<String,Object> userInfoMap = new HashMap();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }
        String token = JwtHelper.createToken(userInfo.getId(), name);
        userInfoMap.put("name",name);
        userInfoMap.put("token",token);
        return userInfoMap;
    }

    @Override
    public UserInfo getUserInfo(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        return userInfo;
    }

    @Override
    public Page<UserInfo> getPageUserInfo(Integer pageNum, Integer limit, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> page = new Page<UserInfo>(pageNum,limit);
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(userInfoQueryVo.getKeyword())){
            wrapper.like("name",userInfoQueryVo.getKeyword()).or().eq("phone",userInfoQueryVo.getKeyword());
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getStatus())){
            wrapper.eq("status",userInfoQueryVo.getStatus());
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getAuthStatus())){
            wrapper.eq("auth_status",userInfoQueryVo.getAuthStatus());
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeBegin())){
            wrapper.gt("create_time",userInfoQueryVo.getCreateTimeBegin());
        }
        if (!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeEnd())){
            wrapper.lt("create_time",userInfoQueryVo.getCreateTimeEnd());
        }


        Page<UserInfo> page1 = baseMapper.selectPage(page, wrapper);
        page1.getRecords().stream().forEach(item->{
            this.packageUserInfo(item);
        });
        return page1;
    }

    @Override
    public void updateStatus(Long id, Integer authStatus) {
        UserInfo userInfo = baseMapper.selectById(id);
        userInfo.setStatus(authStatus);
        baseMapper.updateById(userInfo);
    }

    @Override
    public Map<String, Object> detail(Long id) {
        UserInfo userInfo = baseMapper.selectById(id);
        QueryWrapper<Patient> wrapper = new QueryWrapper<Patient>();
        wrapper.eq("user_id",id);
        List<Patient> patientList = patientService.selectList(wrapper);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userInfo",userInfo);
        map.put("patientList",patientList);
        return map;
    }

    @Override
    public void updateAuthStatus(Long id, Integer authStatus) {
        UserInfo userInfo = baseMapper.selectById(id);
        userInfo.setAuthStatus(authStatus);
        baseMapper.updateById(userInfo);
    }

    private void packageUserInfo(UserInfo item) {
        item.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(item.getAuthStatus()));
        item.getParam().put("statusString", StatusEnum.getStatusNameByStatus(item.getStatus()));
    }
}
