package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;


/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-09-11
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long submitOrder(String scheduleId, String patientId);

    Page<OrderInfo> getOrderPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo);

    OrderInfo detail(Long orderId);

    void cancelOrder(Long orderId);

    void patientRemind();

    Map<String, Object> statistics(OrderCountQueryVo orderCountQueryVo);
}
