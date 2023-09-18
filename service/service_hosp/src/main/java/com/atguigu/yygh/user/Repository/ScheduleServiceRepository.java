package com.atguigu.yygh.user.Repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleServiceRepository extends MongoRepository<Schedule,String> {
    Schedule findScheduleByHosScheduleId(String hosScheduleId);
    List<Schedule> findByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workdate);

    Schedule findByHosScheduleId(String scheduleId);
}
