package com.atguigu.yygh.user.controller.api;

import com.atguigu.yygh.user.bean.Result;
import com.atguigu.yygh.user.service.ScheduleService;
import com.atguigu.yygh.user.utils.HttpRequestHelper;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiScheduleController {
    @Autowired
    private ScheduleService scheduleService;
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(parameterMap);
        scheduleService.save(resultMap);
        return Result.ok();
    }
    ///schedule/list
    @PostMapping("/schedule/list")
    public Result<Page> scheduleList(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(parameterMap);
        String hosScheduleId = (String)resultMap.get("hosScheduleId");
        Page<Schedule> schedulePage = scheduleService.findScheduleByHosScheduleId(resultMap);
        return Result.ok(schedulePage);
    }
    @PostMapping("/schedule/remove")
    public Result remove(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(parameterMap);
        scheduleService.remove(resultMap);
        return Result.ok();
    }
}
