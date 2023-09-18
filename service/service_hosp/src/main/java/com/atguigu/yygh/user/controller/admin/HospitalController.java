package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.user.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/hospital")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;
    @GetMapping("detail/{id}")
    public R detail(@PathVariable String id){
        Hospital hospital = hospitalService.detail(id);
        System.out.println(hospital.getHosname());
        System.out.println(hospital.getHostype());
        return R.ok().data("hospital",hospital);
    }

    @PutMapping("/updateStatus/{id}/{status}")
    public R updateStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return R.ok();
    }
    @GetMapping("/{pageNum}/{pageSize}")
    public R findHospList(@PathVariable Integer pageNum, @PathVariable Integer pageSize, HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage =  hospitalService.findHospList(pageNum,pageSize,hospitalQueryVo);
        return R.ok().data("total",hospitalPage.getTotalElements()).data("list",hospitalPage.getContent());
    }
}
