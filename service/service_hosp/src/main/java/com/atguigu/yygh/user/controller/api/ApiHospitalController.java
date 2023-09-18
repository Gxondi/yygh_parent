package com.atguigu.yygh.user.controller.api;

import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.user.bean.Result;
import com.atguigu.yygh.user.service.HospitalService;
import com.atguigu.yygh.user.utils.HttpRequestHelper;
import com.atguigu.yygh.user.utils.MD5;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {
    @Autowired
    private HospitalService hospitalService;
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request){
        Map<String, Object> ResultMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String requestSignKey = (String)ResultMap.get("sign");
        String RequestHoscode = (String) ResultMap.get("hoscode");
        String platformStringSign = hospitalService.getStringSignByHoscode(RequestHoscode);
        MD5.encrypt(platformStringSign);
        if (!requestSignKey.isEmpty() && !requestSignKey.equals(platformStringSign) && !platformStringSign.isEmpty()){
            String logoData = (String)ResultMap.get("logoData");
            String result = logoData.replaceAll(" ","+");
            ResultMap.put("logoData",result);
            //调用service方法
            hospitalService.save(ResultMap);
            return Result.ok();
        }else {
            throw new yyghException(20001,"签名错误");
        }
    }
    @PostMapping("/hospital/show")
    public Result<Hospital> getHospitalInfo(HttpServletRequest request){
        Map<String, Object> ResultMap = HttpRequestHelper.switchMap(request.getParameterMap());

        Hospital hospital = hospitalService.getHospitalByHosCode((String)ResultMap.get("hoscode"));

        return Result.ok(hospital);
    }
}
