package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {

    void save(Map<String, Object> resultMap);

    String getStringSignByHoscode(String hoscode);

    Hospital getHospitalByHosCode(String hoscode);
    Hospital getHospitalByHoscode(String hoscode);
    Page<Hospital> findHospList(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Hospital detail(String id);

    List<Hospital> findByNameLike(String name);

    Hospital getHospitalDetail(String hoscode);
}
