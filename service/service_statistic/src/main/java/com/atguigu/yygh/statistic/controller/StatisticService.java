package com.atguigu.yygh.statistic.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.statistic.service.StatisticsServiceImpl;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/statistic")

public class StatisticService {
    @Autowired
    private StatisticsServiceImpl statisticService;
    @GetMapping("/countByDate")
    public R statistics(OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> map = statisticService.statistics(orderCountQueryVo);
        if (map == null) {
            System.out.println("没有数据");
            return R.error().message("没有数据");
        }
        return R.ok().data(map);
    }
}
