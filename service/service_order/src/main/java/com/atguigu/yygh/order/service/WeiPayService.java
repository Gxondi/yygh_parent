package com.atguigu.yygh.order.service;

import java.util.Map;

public interface WeiPayService {
    String createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);

    Boolean refund(Long orderId);
}
