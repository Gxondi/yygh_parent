package com.atguigu.yygh.user.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.user.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hosp/schedule")
public class UserScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{scheduleId}")
    public ScheduleOrderVo getSchedule(@PathVariable(value = "scheduleId") String scheduleId){

        return scheduleService.getSchedule(scheduleId);
    }
    @GetMapping("/info/{scheduleId}")
    public R getScheduleInfo(@PathVariable String scheduleId){
        Schedule schedule = scheduleService.getScheduleInfo(scheduleId);
        return R.ok().data("schedule",schedule);
    }
    @GetMapping("/{hoscode}/{depcode}/{pageNum}/{pageSize}")
    public R getSchedulePage(@PathVariable String hoscode,
                         @PathVariable String depcode,
                         @PathVariable Integer pageNum,
                         @PathVariable Integer pageSize){
        Map<String,Object> map = scheduleService.getSchedulePage(hoscode,depcode,pageNum,pageSize);
        return R.ok().data(map);
    }
    @GetMapping("/{hoscode}/{depcode}/{workDate}")
    public R detail(@PathVariable String hoscode,
                    @PathVariable String depcode,
                    @PathVariable String workDate){
        List<Schedule> details = scheduleService.detail(hoscode, depcode, workDate);

        return R.ok().data("details",details);
    }
}
