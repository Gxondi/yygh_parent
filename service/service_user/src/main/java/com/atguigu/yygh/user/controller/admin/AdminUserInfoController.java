package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/administer/userinfo")
public class AdminUserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("/{pageNum}/{limit}")
    public R getPageUserInfo(@PathVariable Integer pageNum, @PathVariable Integer limit, UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> page = userInfoService.getPageUserInfo(pageNum,limit,userInfoQueryVo);
        return R.ok().data("total",page.getTotal()).data("list",page.getRecords());
    }
    @PutMapping("/{id}/{status}")
    public R updateSatus(@PathVariable Long id ,@PathVariable Integer status){
        userInfoService.updateStatus(id,status);
        return R.ok();
    }
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id){
        Map<String,Object> map = userInfoService.detail(id);
        return R.ok().data(map);
    }
    @PutMapping("/auth/{id}/{authStatus}")
    public R approval(@PathVariable Long id ,@PathVariable Integer authStatus){
        userInfoService.updateAuthStatus(id,authStatus);
        return R.ok();
    }
}
