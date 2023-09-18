package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.mapper.RefundInfoMapper;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 退款信息表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-09-15
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {

        //保存之前先查询一下库里是否存在次退款订单
        Long orderId = paymentInfo.getOrderId();
        QueryWrapper<RefundInfo> queryWrapper=new QueryWrapper<RefundInfo>();
        queryWrapper.eq("order_id",orderId);
        RefundInfo refundInfo1 = baseMapper.selectOne(queryWrapper);
        if(refundInfo1 != null){
            return refundInfo1;
        }

        RefundInfo refundInfo=new RefundInfo();
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        refundInfo.setSubject("想退款...");
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
