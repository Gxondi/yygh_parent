package com.atguigu.yygh.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.user.Repository.ScheduleServiceRepository;
import com.atguigu.yygh.user.service.HospitalService;
import com.atguigu.yygh.user.service.ScheduleService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleServiceRepository scheduleServiceRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentServiceImpl departmentService;
    @Override
    public void save(Map<String, Object> resultMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Schedule.class);
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        String hosScheduleId = schedule.getHosScheduleId();
        Schedule schedule1 = scheduleServiceRepository.findScheduleByHosScheduleId(hosScheduleId);
        if (schedule1 == null) {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setStatus(1);
            scheduleServiceRepository.save(schedule);
        }else {
            schedule.setUpdateTime(new Date());
            schedule.setCreateTime(schedule1.getCreateTime());
            schedule.setStatus(schedule1.getStatus());
            schedule.setId(schedule1.getId());
            scheduleServiceRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> findScheduleByHosScheduleId(Map<String, Object> resultMap) {
        String hosScheduleId = (String)resultMap.get("hosScheduleId");
        Integer page = Integer.parseInt((String) resultMap.get("page"));//当前页
        Integer limit = Integer.parseInt((String) resultMap.get("limit"));//每页记录数
        Schedule schedule = new Schedule();
        Example example = Example.of(schedule);//封装查询条件
        Pageable pageable = PageRequest.of(page, limit);//分页
        return scheduleServiceRepository.findAll(example, pageable);
    }

    @Override
    public void remove(Map<String, Object> resultMap) {
        String hosScheduleId = (String)resultMap.get("hosScheduleId");
        Schedule scheduleTime = scheduleServiceRepository.findScheduleByHosScheduleId(hosScheduleId);
        if (scheduleTime!=null){
            scheduleServiceRepository.deleteById(scheduleTime.getId());
        }
    }

    @Override
    public Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode) {


        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //聚合:最好使用mongoTemplate
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC, "workDate"),
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)

        );
        /*=============================================
              第一个参数Aggregation：表示聚合条件
              第二个参数InputType： 表示输入类型，可以根据当前指定的字节码找到mongo对应集合
              第三个参数OutputType： 表示输出类型，封装聚合后的信息
          ============================================*/
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        //当前页对应的列表数据
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        for (BookingScheduleRuleVo bookingScheduleRuleVo : mappedResults) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            //工具类：美年旅游：周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        Aggregation aggregation2 = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate"));
        /*=============================================
              第一个参数Aggregation：表示聚合条件
              第二个参数InputType： 表示输入类型，可以根据当前指定的字节码找到mongo对应集合
              第三个参数OutputType： 表示输出类型，封装聚合后的信息
          ============================================*/
        AggregationResults<BookingScheduleRuleVo> aggregate2 = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);

        Map<String, Object> map=new HashMap<String,Object>();
        map.put("list",mappedResults);
        map.put("total",aggregate2.getMappedResults().size());

        //获取医院名称
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());

        map.put("baseMap",baseMap);

        return map;
    }

    //封装排班详情其他值 医院名称、科室名称、日期对应星期
    @Override
    public List<Schedule> detail(String hoscode, String depcode, String workdate) {
        Date date = new DateTime(workdate).toDate();
        List<Schedule> scheduleList =scheduleServiceRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,date);
        //把得到list集合遍历，向设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });
        return scheduleList;
    }

    @Override
    public Map<String, Object> getSchedulePage(String hoscode, String depcode, Integer pageNum, Integer pageSize) {
        Hospital hospital = hospitalService.getHospitalByHosCode(hoscode);
        if(hospital == null){
            throw new yyghException(20001,"医院不存在");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //获取可预约日期的数据（分页）
        IPage<Date> page = this.getListDate(pageNum,pageSize,bookingRule);
        List<Date> records = page.getRecords();


        Criteria criteria=Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(records);


        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();

        Map<Date, BookingScheduleRuleVo> collect = mappedResults.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
        int size = records.size();

        List<BookingScheduleRuleVo> bookingScheduleRuleVoList=new ArrayList<BookingScheduleRuleVo>();

        for(int i=0;i<size;i++){
            Date date = records.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(date);
            if(bookingScheduleRuleVo == null){
                bookingScheduleRuleVo=new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(date);
                //bookingScheduleRuleVo.setWorkDateMd(date);
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setReservedNumber(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);//当天所有医生的总的剩余可预约数
                //bookingScheduleRuleVo.setStatus(0);
            }


            bookingScheduleRuleVo.setWorkDateMd(date);
            bookingScheduleRuleVo.setDayOfWeek(this.getDayOfWeek(new DateTime(date)));
            bookingScheduleRuleVo.setStatus(0); //

            //第一页第一条做特殊判断处理
            if(i==0 && pageNum == 1){
                DateTime dateTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                //如果医院规定的当前的挂号截止时间在此时此刻之前，说明：此时此刻已经过了当天的挂号截止时间了
                if(dateTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            //最后一页的最后一条做特殊判断处理
            if(pageNum==page.getPages() && i== (size-1) ){
                bookingScheduleRuleVo.setStatus(1);
            }

            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("total",page.getTotal());
        map.put("list",bookingScheduleRuleVoList);

        Map<String,Object> baseMap = new HashMap<String,Object>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospitalByHosCode(hoscode).getHosname());
        System.out.println("getHospitalByHosCode: " + hospitalService.getHospitalByHoscode(hoscode).getHosname());
        System.out.println("getHospitalByHoscode: " + hospitalService.getHospitalByHoscode(hoscode).getHosname());
        //科室
        Department department=departmentService.getDepartment(hoscode,depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());

        map.put("baseMap",baseMap);

        return map;
    }

    @Override
    public Schedule getScheduleInfo(String scheduleId) {
        Schedule schedule = scheduleServiceRepository.findById(scheduleId).get();
        this.packageSchedule(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getSchedule(String scheduleId) {
        Schedule schedule = scheduleServiceRepository.findById(scheduleId).get();
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        BeanUtils.copyProperties(schedule,scheduleOrderVo);
        Hospital hospital = hospitalService.getHospitalByHosCode(schedule.getHoscode());

        scheduleOrderVo.setHosname(hospital.getHosname());
        Department department = departmentService.getDepartment(schedule.getHoscode(), schedule.getDepcode());
        scheduleOrderVo.setDepname(department.getDepname());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        //预约日期
        Integer quitDay = hospital.getBookingRule().getQuitDay();//退号日期
        String quitTime = hospital.getBookingRule().getQuitTime();//退号时间

        DateTime dateTime = getDateTime(new DateTime(schedule.getWorkDate()).plusDays(hospital.getBookingRule().getQuitDay()).toDate(), hospital.getBookingRule().getQuitTime());
        Date workDate = schedule.getWorkDate();
        DateTime dateTime1 = this.getDateTime(workDate, hospital.getBookingRule().getReleaseTime());
        scheduleOrderVo.setStopTime(dateTime1.toDate());
        return scheduleOrderVo;
    }

    @Override
    public boolean updateAvailableNumber(String scheduleId, Integer availableNumber) {
        Schedule schedule = scheduleServiceRepository.findById(scheduleId).get();
        if (schedule != null) {
            schedule.setAvailableNumber(availableNumber);
            schedule.setCreateTime(new Date());
            scheduleServiceRepository.save(schedule);
            return true;
        }
        return false;
    }

    @Override
    public void concelSchedule(String scheduleId) {
        Schedule scheduleId1 = scheduleServiceRepository.findByHosScheduleId(scheduleId);
        scheduleId1.setAvailableNumber( scheduleId1.getAvailableNumber()+1);
        scheduleServiceRepository.save(scheduleId1);
    }


    private IPage getListDate(Integer pageNum, Integer pageSize, BookingRule bookingRule) {

        Integer cycle = bookingRule.getCycle();
        //如果当前时间已经超过了放号时间，周期加1
        String releaseTime = bookingRule.getReleaseTime();
        //放号时间
        DateTime dateTime = this.getDateTime(new Date(),releaseTime);
        if (dateTime.isBeforeNow()) {
            cycle+=1;
        }
        //获取可预约的所有日期，最后一天显示即可
        List<Date> list = new ArrayList<>();
        for (int i = 0; i < cycle; i++) {
            list.add(new DateTime(new DateTime().plusDays(i).toString("yyyy-MM-dd")).toDate());
        }
        //根据工作日期获取可预约的排班数据
        int start = (pageNum - 1)*pageSize;
        //如果start超过了list的长度，就显示最后一页
        int end = start+pageSize;
        //如果end超过了list的长度，就显示最后一页
        if (end > list.size()) {
            end = list.size();
        }
        //当前页的数据
        List<Date> currentPageDateList = new ArrayList<Date>();
        //遍历当前页的数据
        for (int j = start; j < end; j++) {
            Date date = list.get(j);
            currentPageDateList.add(date);

        }
        //获取当前页可预约的排班数据
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Date> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Date>(pageNum, pageSize, list.size());
        page.setRecords(currentPageDateList);
        return page;
    }
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospitalByHoscode(schedule.getHoscode()).getHosname());
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }



    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}


