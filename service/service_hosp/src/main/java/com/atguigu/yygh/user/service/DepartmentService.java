package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> resultMap);


    Page<Department> findDepartmentByHosCodeAndDepcode(Map<String, Object> resultMap);

    void remove(Map<String, Object> resultMap);

    List<DepartmentVo> getDepartmentList(String hoscode);

    String getDepName(String hoscode, String depcode);

    Department getDepartment(String hoscode, String depcode);
}
