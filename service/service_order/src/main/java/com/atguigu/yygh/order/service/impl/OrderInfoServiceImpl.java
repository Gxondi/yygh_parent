package com.atguigu.yygh.order.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.mq.MqConst;
import com.atguigu.yygh.mq.RabbitService;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.PaymentService;
import com.atguigu.yygh.order.service.WeiPayService;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.user.client.ScheduleFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.aspectj.weaver.ast.Or;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-09-11
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private ScheduleFeignClient scheduleFeignClient;
    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private WeiPayService  weiPayService;
    @Autowired
    private PaymentService paymentService;
    @Override
    public Long submitOrder(String scheduleId, String patientId) {
        //1.根据排班id获取排班信息
        ScheduleOrderVo schedule = scheduleFeignClient.getSchedule(scheduleId);
        if(new DateTime(schedule.getStopTime()).isBeforeNow()){
            throw new yyghException(20001,"超过了挂号截止时间");
        }
        //2.根据就诊人id获取就诊人信息
        Patient patient = patientFeignClient.getPatient(patientId);
        //3.从平台请求第三方医院，是否可以挂号

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",schedule.getHoscode());
        paramMap.put("depcode",schedule.getDepcode());
        paramMap.put("hosScheduleId",schedule.getHosScheduleId());
        paramMap.put("reserveDate",schedule.getReserveDate());
        paramMap.put("reserveTime",schedule.getReserveTime());
        paramMap.put("amount",schedule.getAmount());
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998//order/submitOrder");


        if(jsonObject != null || jsonObject.getInteger("code") == 200){
            //5.可以返回医生排班信息，就诊人信息，放进sql表中
            JSONObject data = jsonObject.getJSONObject("data");
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setUserId(patient.getUserId());
            String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setHoscode(schedule.getHoscode());
            orderInfo.setHosname(schedule.getHosname());
            orderInfo.setDepcode(schedule.getDepcode());
            orderInfo.setDepname(schedule.getDepname());
            orderInfo.setTitle(schedule.getTitle());
            orderInfo.setScheduleId(schedule.getHosScheduleId());
            orderInfo.setReserveDate(schedule.getReserveDate());
            orderInfo.setReserveTime(schedule.getReserveTime());
            orderInfo.setPatientId(patient.getUserId());
            orderInfo.setPatientName(patient.getName());
            orderInfo.setHosRecordId(data.getString("hosRecordId"));//医院的订单号
            orderInfo.setNumber(data.getInteger("number"));//取号需要
            orderInfo.setFetchTime(data.getString("fetchTime"));//取号时间段
            orderInfo.setFetchAddress(data.getString("fetchAddress"));//取号地址
            orderInfo.setAmount(schedule.getAmount());
            orderInfo.setQuitTime(schedule.getQuitTime());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
            baseMapper.insert(orderInfo);

            //6.更新排班剩余预约数
            OrderMqVo orderMqVo=new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            //int reservedNumber = data.getIntValue("reservedNumber");
            int availableNumber = data.getIntValue("availableNumber");
            orderMqVo.setAvailableNumber(availableNumber);

            MsmVo msmVo=new MsmVo();
            msmVo.setPhone(patient.getPhone());

            msmVo.setTemplateCode("您已经预约了上午${time}点的${name}医生的号，不要迟到!");
            Map<String,Object> msmMap=new HashMap<String, Object>();
            msmMap.put("time",schedule.getReserveDate()+" "+schedule.getReserveTime());
            msmMap.put("name","xxx");
            msmVo.setParam(msmMap);
            orderMqVo.setMsmVo(msmVo);
            if (orderMqVo==null){
                System.out.println("orderMqVo为空");
                return null;
            }
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);
            //3.4 给就诊人发送短信提醒


            //4.返回订单的id
            return orderInfo.getId();

        }else {
            //4.不可以,返回异常
            throw new RuntimeException("调用医院接口失败");
        }

        //7.给就诊人发送短信
    }

    @Override
    public Page<OrderInfo> getOrderPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo) {
        Page page = new Page(pageNum,pageSize);
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        Long userId = orderQueryVo.getUserId(); //用户id
        String outTradeNo = orderQueryVo.getOutTradeNo();//订单号
        String keyword = orderQueryVo.getKeyword();//医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人id
        String orderStatus = orderQueryVo.getOrderStatus();//订单状态
        String reserveDate = orderQueryVo.getReserveDate(); //预约日期
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();//下订单时间
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();//下订单时间


        if(!StringUtils.isEmpty(userId)){
            queryWrapper.eq("user_id", userId);
        }
        if(!StringUtils.isEmpty(outTradeNo)){
            queryWrapper.eq("out_trade_no", outTradeNo);
        }
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper.like("hosname", keyword);
        }
        if(!StringUtils.isEmpty(patientId)){
            queryWrapper.eq("patient_id", patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)){
            queryWrapper.eq("order_status", orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)){
            queryWrapper.ge("reserve_date", reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.ge("create_time", createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le("create_time", createTimeEnd);
        }
        Page<OrderInfo> page1 = baseMapper.selectPage(page, queryWrapper);
        page1.getRecords().parallelStream().forEach(item->{
            this.packageOrderInfo(item);
        });

        return page1;
    }

    @Override
    public OrderInfo detail(Long orderId) {

        OrderInfo orderInfo = baseMapper.selectById(orderId);
        this.packageOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        //1.确定当前取消预约的时间 和 挂号订单的取消预约截止时间 对比, 当前时间是否已经超过了 挂号订单的取消预约截止时间
        //1.1 如果超过了，直接抛出异常，不让用户取消
        if(quitTime.isBeforeNow()){
            throw  new yyghException(20001,"超过了退号的截止时间");
        }

        Map<String,Object>  hospitalParamMap=new HashMap<String,Object>();
        hospitalParamMap.put("hoscode",orderInfo.getHoscode());
        hospitalParamMap.put("hosRecordId",orderInfo.getHosRecordId());


        //2.从平台请求第三方医院，通知第三方医院，该用户已取消
        JSONObject jsonObject = HttpRequestHelper.sendRequest(hospitalParamMap, "http://localhost:9998/order/updateCancelStatus");
        //2.1 第三方医院如果不同意取消：抛出异常，不能取消
        if(jsonObject == null || jsonObject.getIntValue("code") != 200){
            throw  new yyghException(20001,"取消失败");
        }
        //3.判断用户是否对当前挂号订单是否已支付
        if(orderInfo.getOrderStatus() == OrderStatusEnum.PAID.getStatus()){
            //3.1.如果已支付，退款
            boolean flag= weiPayService.refund(orderId);
            if(!flag){
                throw new yyghException(20001,"退款失败");
            }
        }

        //无论用户是否进了支付

        //4.更新订单的订单状态 及 支付记录表的支付状态
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);

        UpdateWrapper<PaymentInfo> updateWrapper=new UpdateWrapper<PaymentInfo>();
        updateWrapper.eq("order_id",orderInfo.getId());
        updateWrapper.set("payment_status", PaymentStatusEnum.REFUND.getStatus());
        paymentService.update(updateWrapper);

        //5.更新医生的剩余可预约数信息

        OrderMqVo orderMqVo=new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        MsmVo msmVo=new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        msmVo.setTemplateCode("xxxx.....");
        msmVo.setParam(null);
        orderMqVo.setMsmVo(msmVo);
        //6.给就诊人发送短信提示：
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);
    }

    @Override
    public void patientRemind() {
        QueryWrapper<OrderInfo> queryWrapper=new QueryWrapper<OrderInfo>();
        queryWrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        queryWrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());

        List<OrderInfo> orderInfos = baseMapper.selectList(queryWrapper);
        for(OrderInfo orderInfo : orderInfos) {
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS, MqConst.ROUTING_SMS_ITEM, msmVo);
        }
    }

    @Override
    public Map<String, Object> statistics(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> conuntVoList = baseMapper.statistics(orderCountQueryVo);
//        List<String> dateList = new ArrayList<>();
//        List<Integer> countList = new ArrayList<>();
//        for (OrderCountVo orderCountVo : conuntVoList) {
//            Integer count = orderCountVo.getCount();
//            String reserveDate = orderCountVo.getReserveDate();
//            dateList.add(reserveDate);
//            countList.add(count);
//        }
        List<String> dateList = conuntVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        List<Integer> countList = conuntVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        Map<String,Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    private void packageOrderInfo(OrderInfo item) {
        item.getParam().put("orderStatusString",OrderStatusEnum.getStatusNameByStatus(item.getOrderStatus()));
    }


}
