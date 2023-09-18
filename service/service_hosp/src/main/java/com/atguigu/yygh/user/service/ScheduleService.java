package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> resultMap);

    Page<Schedule> findScheduleByHosScheduleId(Map<String, Object> resultMap);

    void remove(Map<String, Object> resultMap);

    Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    //List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    List<Schedule> detail(String hoscode, String depcode, String workdate);

    Map<String, Object> getSchedulePage(String hoscode, String depcode, Integer pageNum, Integer pageSize);

    Schedule  getScheduleInfo(String scheduleId);

    ScheduleOrderVo getSchedule(String scheduleId);

    boolean updateAvailableNumber(String scheduleId, Integer availableNumber);

    void concelSchedule(String scheduleId);
}
