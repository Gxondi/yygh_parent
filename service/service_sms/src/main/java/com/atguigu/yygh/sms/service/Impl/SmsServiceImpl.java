package com.atguigu.yygh.sms.service.Impl;

import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.sms.utils.HttpUtils;
import com.atguigu.yygh.sms.utils.RandomUtil;
import com.atguigu.yygh.vo.msm.MsmVo;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*====================================================
                时间: 2022-06-01
                讲师: 刘  辉
                出品: 尚硅谷教学团队
======================================================*/
@Service
public class SmsServiceImpl  implements SmsService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean sendCode(String phone) {
        String redisCode = (String)redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(redisCode)){
            return true;
        }
        String host = "http://notifysms.market.alicloudapi.com";
        String path = "/send";
        String method = "POST";
        String appcode = "4176d7333d97424bb2ed66251f696ffe";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        String fourBitRandom = RandomUtil.getFourBitRandom();
        bodys.put("mobile", phone);
        bodys.put("template_code", "T0001");
        bodys.put("params", "{\"code\":" + fourBitRandom + "}");
        bodys.put("sign_name", "复数科技");


        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));

            //把验证码保存redis中一份
            redisTemplate.opsForValue().set(phone,fourBitRandom,15, TimeUnit.DAYS);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void sendMessage(MsmVo msmVo) {
        String phone = msmVo.getPhone();
        System.out.println("给就诊人发送成功");
    }


}
