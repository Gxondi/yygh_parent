package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.client.DictFeignClient;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-09-05
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
    @Autowired
    private DictFeignClient dictFeignClient;
    @Override
    public List<Patient> findAll(String token) {
        Long userId = JwtHelper.getUserId(token);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",userId);
        List<Patient> patientList = baseMapper.selectList(queryWrapper);
        patientList.stream().forEach(item ->{
            this.packPatient(item);
        });
        return patientList;
    }

    @Override
    public Patient detail(Long id) {
        Patient patient = baseMapper.selectById(id);
        this.packPatient(patient);
        return patient;
    }

    @Override
    public List<Patient> selectList(QueryWrapper<Patient> wrapper) {
        List<Patient> patientList = baseMapper.selectList(wrapper);
        patientList.stream().forEach(item ->{
            this.packPatient(item);
        });
        return patientList;
    }

    private void packPatient(Patient item){

        item.getParam().put("certificatesTypeString",dictFeignClient.getNameByValue(Long.parseLong(item.getCertificatesType())));
        String province = dictFeignClient.getNameByValue(Long.parseLong(item.getProvinceCode()));
        item.getParam().put("provinceString",province);
        String city = dictFeignClient.getNameByValue(Long.parseLong(item.getCityCode()));
        item.getParam().put("cityString",city);
        String district = dictFeignClient.getNameByValue(Long.parseLong(item.getDistrictCode()));
        item.getParam().put("districtSting",district);
        item.getParam().put("fullAddress",province+city+district+item.getAddress());

    }
}
