package com.atguigu.yygh.statistic.service;

import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.order.client.OrderFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatisticsServiceImpl {
    @Autowired
    private OrderFeignClient orderFeignClient;
    public Map<String, Object> statistics(OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> statistics = orderFeignClient.statistics(orderCountQueryVo);
        return statistics;
    }
}
