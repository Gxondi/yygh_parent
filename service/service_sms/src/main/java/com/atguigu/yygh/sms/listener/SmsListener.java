package com.atguigu.yygh.sms.listener;

import com.atguigu.yygh.mq.MqConst;
import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.vo.msm.MsmVo;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsListener {
    @Autowired
    private SmsService smsService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_SMS, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_SMS),
            key = {MqConst.ROUTING_SMS_ITEM}
    ))
    public void consumer(MsmVo msmVo) {
        smsService.sendMessage(msmVo);
    }
}
