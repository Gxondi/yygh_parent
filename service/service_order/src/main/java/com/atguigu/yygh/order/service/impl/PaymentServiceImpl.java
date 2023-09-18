package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.order.mapper.PaymentMapper;
import com.atguigu.yygh.order.service.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentType) {
        PaymentInfo paymentInfo = new PaymentInfo();
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no",order.getOutTradeNo());
        wrapper.eq("payment_type",paymentType);
        PaymentInfo paymentInfo1 = baseMapper.selectOne(wrapper);
        if (paymentInfo1!=null){
            return;
        }
        paymentInfo.setOutTradeNo(order.getOutTradeNo());//订单唯一编号
        paymentInfo.setOrderId(order.getId());//订单编号
        paymentInfo.setPaymentType(paymentType);//支付类型（1：微信 2：支付宝）
        paymentInfo.setTotalAmount(order.getAmount());//金额

        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd")+"|"+order.getHosname()+"|"+order.getDepname()+"|"+order.getTitle();//订单名称
        paymentInfo.setSubject(subject);
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());//支付状态
        baseMapper.insert(paymentInfo);
    }
}