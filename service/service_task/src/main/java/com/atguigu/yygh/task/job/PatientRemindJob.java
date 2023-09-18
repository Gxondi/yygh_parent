package com.atguigu.yygh.task.job;

import com.atguigu.yygh.mq.MqConst;
import com.atguigu.yygh.mq.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PatientRemindJob {
    @Autowired
    private RabbitService rabbitService;
    @Scheduled(cron = "* * * * ?")
    public void patientRemind() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_8, "");

        System.out.println("开始执行短信发送");
    }
}
