package com.atguigu.yygh.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.order.prop.WeiPayProperties;
import com.atguigu.yygh.order.service.OrderInfoService;

import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.WeiPayService;
import com.atguigu.yygh.order.utils.HttpClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/order/weixin")
public class WeiPayController {
    @Autowired
    private WeiPayService weiPayService;
    @Autowired
    private OrderInfoService orderInfoservice;
    @Autowired
    private PaymentService paymentService;
    @GetMapping("/{orderId}")
    public R createNative(@PathVariable Long orderId) {
        String url = weiPayService.createNative(orderId);
        return R.ok().data("url", url);
    }
    @GetMapping("/status/{orderId}")
    public R getPayStatus(@PathVariable Long orderId) {
        Map<String,String> map = weiPayService.queryPayStatus(orderId);
        if (map == null) {
            return R.error().message("查询失败");
        }
        if ("SUCCESS".equals(map.get("trade_sate"))){
            //更改订单状态
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setId(orderId);
            orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
            orderInfoservice.updateById(orderInfo);
            //更新支付记录表
            UpdateWrapper updateWrapper=new UpdateWrapper();
            updateWrapper.eq("order_id",orderId);
            updateWrapper.set("trade_no",map.get("transaction_id")); //微信支付的订单号[微信服务器]
            updateWrapper.set("payment_status", PaymentStatusEnum.PAID.getStatus());
            updateWrapper.set("callback_time",new Date());

            updateWrapper.set("callback_content", JSONObject.toJSONString(map));
            paymentService.update(updateWrapper);
            return R.ok();
        }
        return R.ok().message("支付中");
    }
}
