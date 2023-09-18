package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.user.service.DepartmentService;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{hoscode}")
    public R getDepartmentList(@PathVariable String hoscode){
       List<DepartmentVo> departmentVos = departmentService.getDepartmentList(hoscode);
        return R.ok().data("list",departmentVos);
    }
}
