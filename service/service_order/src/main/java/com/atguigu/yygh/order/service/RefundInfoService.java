package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 退款信息表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-09-15
 */
public interface RefundInfoService extends IService<RefundInfo> {

    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
