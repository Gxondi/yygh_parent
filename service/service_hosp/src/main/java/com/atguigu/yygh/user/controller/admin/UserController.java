package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.acl.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
public class UserController {
    //login
    @PostMapping("/login")
    public R login(@RequestBody User user){

        return R.ok().data("token","admin-token");
    }
    @GetMapping("/info")
    public R info(){

        return R.ok().data("roles","[admin]")
                     .data("introduction","Super Administrator")
                     .data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                     .data("name","Super Admin");
    }
}
