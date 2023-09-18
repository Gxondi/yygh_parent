package com.atguigu.yygh.user.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.prop.WeiXinProperties;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/userinfo/wx")
public class WeiXinController {
    @Autowired
    private WeiXinProperties weiXinProperties;
    @Autowired
    private UserInfoService userInfoService;
    @GetMapping("/param")
    @ResponseBody
    public R getWeiXinLoginParam(){
        String url = URLEncoder.encode(weiXinProperties.getRedirecturl());
        Map<String,Object> map = new HashMap<>();
        map.put("appid",weiXinProperties.getAppid());
        map.put("scope","snsapi_login");
        map.put("redirecturl",url);
        map.put("state",System.currentTimeMillis()+"");
        return R.ok().data(map);
    }
    @GetMapping("/callback")
    public String callback(String code,String state) throws Exception {
        StringBuffer append = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String format = String.format(append.toString(),
                weiXinProperties.getAppid(),
                weiXinProperties.getAppsecret(),
                code);

        String result = HttpClientUtils.get(format);
        //System.out.println(result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        String openid = jsonObject.getString("openid");
        //System.out.println(openid);
        String access_token = jsonObject.getString("access_token");
        //System.out.println(access_token);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("openid",openid);
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        if (userInfo == null){
            userInfo = new UserInfo();
            //获取扫描人信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl.toString(), access_token, openid);
            String resultInfo = HttpClientUtils.get(userInfoUrl);
            JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
            String nickname = resultUserInfoJson.getString("nickname");
            //获取扫描人信息添加数据库
            userInfo = new UserInfo();
            userInfo.setNickName(nickname);
            userInfo.setOpenid(openid);
            userInfo.setStatus(1);
            userInfoService.save(userInfo);
        }
        //5.判断status状态
        if (userInfo.getStatus() == 0){
            throw new yyghException(20001,"用户被禁用");
        }
        //6.返回登录信息
        Map<String,String> map = new HashMap();
        if (StringUtils.isEmpty(userInfo.getPhone())){
            map.put("openid",openid);
        }else {
            map.put("openid","");
        }
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)){
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)){
            name = userInfo.getPhone();
        }
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("name",name);
        map.put("token",token);


        return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
    }
}
