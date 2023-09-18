package com.atguigu.yygh.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.user.Repository.DepartmentRepository;
import com.atguigu.yygh.user.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> resultMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Department.class);
        String hoscode = department.getHoscode();
        String depcode = department.getDepcode();
        Department platformDepartment =  departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode,depcode);
        if(platformDepartment ==null){
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            department.setUpdateTime(new Date());
            department.setId(platformDepartment.getId());
            department.setCreateTime(platformDepartment.getCreateTime());
            department.setIsDeleted(platformDepartment.getIsDeleted());
            departmentRepository.save(department);
        }

    }

    @Override
    public Page<Department> findDepartmentByHosCodeAndDepcode(Map<String, Object> resultMap) {
        String hsocode = (String)resultMap.get("hsocode");
        Integer page = Integer.parseInt((String) resultMap.get("page"));
        Integer limit = Integer.parseInt((String) resultMap.get("limit"));
        Department department = new Department();
        Example<Department> example = Example.of(department);
        Pageable pageable = PageRequest.of(page, limit);
        return departmentRepository.findAll(example,pageable);
    }

    @Override
    public void remove(Map<String, Object> resultMap) {
        String hoscode = (String)resultMap.get("hoscode");
        String depcode = (String)resultMap.get("depcode");
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            departmentRepository.deleteById(department.getId());
        }
    }
    //根据医院编号(hoscode)查询所有科室信息
    @Override
    public List<DepartmentVo> getDepartmentList(String hoscode) {
        Department department = new Department();
        department.setHoscode(hoscode);
        Example example = Example.of(department);
        //根据hscodce查询所有科室
        List<Department> all = departmentRepository.findAll(example);
        //根据大科室编号bigcode分组(内科，神经科，骨科等大的种类科室)
        Map<String, List<Department>> collect = all.stream().collect(Collectors.groupingBy(Department::getBigcode));
        ArrayList<DepartmentVo> bigDepartmentVoArrayList = new ArrayList<>();
        for (Map.Entry<String, List<Department>> stringListEntry : collect.entrySet()) {
            DepartmentVo bigDepartmentVo = new DepartmentVo();
            //得到大科室编号
            String bigCode = stringListEntry.getKey();
            //得到大科室编号下的所有子科室
            List<Department> departmentList = stringListEntry.getValue();

            ArrayList<DepartmentVo> childDepartmentVoArrayList = new ArrayList<>();
            for (Department childDepartment : departmentList) {
                DepartmentVo childDepartmentVo = new DepartmentVo();
                childDepartmentVo.setDepname(childDepartment.getDepname());//小科室名称
                childDepartmentVo.setDepcode(childDepartment.getDepcode());//小科室编号
                childDepartmentVoArrayList.add(childDepartmentVo);
            }
            bigDepartmentVo.setDepcode(bigCode);//大科室编号
            bigDepartmentVo.setDepname(departmentList.get(0).getBigname());//大科室名称
            bigDepartmentVo.setChildren(childDepartmentVoArrayList);
            bigDepartmentVoArrayList.add(bigDepartmentVo);
        }
        return bigDepartmentVoArrayList;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {

        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        if(department != null){
            return department.getDepname();
        }
        return "";
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        return department;
    }


}
