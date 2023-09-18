package com.atguigu.yygh.user.client;

import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-hosp")
public interface ScheduleFeignClient {
    @GetMapping("/user/hosp/schedule/{scheduleId}")
    public ScheduleOrderVo getSchedule(@PathVariable(value = "scheduleId") String scheduleId);
}
