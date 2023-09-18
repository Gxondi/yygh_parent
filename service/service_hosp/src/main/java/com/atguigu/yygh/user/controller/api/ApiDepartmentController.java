package com.atguigu.yygh.user.controller.api;

import com.atguigu.yygh.user.bean.Result;
import com.atguigu.yygh.user.service.DepartmentService;
import com.atguigu.yygh.user.utils.HttpRequestHelper;
import com.atguigu.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiDepartmentController {
    @Autowired
    private DepartmentService departmentService;
    @PostMapping("/department/remove")
    public Result remove(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(parameterMap);
        departmentService.remove(resultMap);
        return Result.ok();

    }
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(parameterMap);
        departmentService.save(resultMap);
        return Result.ok();
    }
    @PostMapping("/department/list")
    public Result<Page> getDepartment(HttpServletRequest request){

        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(parameterMap);

        String hoscode = (String)resultMap.get("hoscode");//医院编号

        Page<Department> departmentPage = departmentService.findDepartmentByHosCodeAndDepcode(resultMap);//根据医院编号和科室编号查询
        return Result.ok(departmentPage);
    }
}
