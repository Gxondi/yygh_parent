package com.atguigu.yygh.order.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-09-11
 */
@RestController
@RequestMapping("/api/order/orderInfo")

public class OrderInfoController {
    @Autowired
    private OrderInfoService orderService;

    @PostMapping("/{scheduleId}/{patientId}")
    public R submitOrder(@PathVariable String scheduleId,
                       @PathVariable String patientId) {
        Long orderId = orderService.submitOrder(scheduleId, patientId);
        return R.ok().data("orderId", orderId);
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public R getOrderInfoPage(@PathVariable Integer pageNum,
                          @PathVariable Integer pageSize,
                          OrderQueryVo orderQueryVo,
                          @RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo> page = orderService.getOrderPage(pageNum,pageSize,orderQueryVo);

        return R.ok().data("page",page);
    }
    @GetMapping("/list")
    public R getStatusList(){
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();
        return R.ok().data("list", statusList);
    }
    @GetMapping("/{orderId}")
    public R getOrderList(@PathVariable Long orderId){
        OrderInfo orderInfo = orderService.detail(orderId);
        return R.ok().data("orderInfo", orderInfo);
    }
    @GetMapping("/cancelOrder/{orderId}")
    public R cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return R.ok();
    }
    @PostMapping("/statistics")
    public Map<String,Object> statistics(@RequestBody OrderCountQueryVo orderCountQueryVo){
        Map<String, Object> map = orderService.statistics(orderCountQueryVo);
        if (map == null) {
            System.out.println("没有数据");
            return null;
        }
        return map;
    }

}

