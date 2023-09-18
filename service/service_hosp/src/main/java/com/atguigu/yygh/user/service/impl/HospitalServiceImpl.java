package com.atguigu.yygh.user.service.impl;

import com.alibaba.fastjson.JSONObject;

import com.atguigu.yygh.client.DictFeignClient;
import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.user.Repository.HospitalRepository;
import com.atguigu.yygh.user.mapper.HospitalSetMapper;
import com.atguigu.yygh.user.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Autowired
    private HospitalSetMapper hospitalSetMapper;
    @Override
    public void save(Map<String, Object> resultMap) {
        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(resultMap), Hospital.class);
        String hoscode = hospital.getHoscode();
        Hospital collection = hospitalRepository.findByHoscode(hoscode);
        if (collection == null){
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {
            hospital.setUpdateTime(new Date());
            hospital.setCreateTime(collection.getCreateTime());
            hospital.setIsDeleted(collection.getIsDeleted());
            hospital.setStatus(collection.getStatus());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public String getStringSignByHoscode(String requestHoscode) {
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<HospitalSet>();
        queryWrapper.eq("hoscode",requestHoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(queryWrapper);
        if (hospitalSet == null){
            throw new yyghException(20001,"医院不存在");
        }
        return hospitalSet.getSignKey();
    }

    @Override
    public Hospital getHospitalByHosCode(String requestHoscode) {
        return hospitalRepository.findByHoscode(requestHoscode);
    }

    @Override
    public Hospital getHospitalByHoscode(String requestHoscode) {
        return hospitalRepository.findByHoscode(requestHoscode);
    }

    @Override
    public Page<Hospital> findHospList(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {
        Hospital hospital=new Hospital();
        if(!StringUtils.isEmpty(hospitalQueryVo.getHosname())){
            hospital.setHosname(hospitalQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hospitalQueryVo.getProvinceCode())){
            hospital.setProvinceCode(hospitalQueryVo.getProvinceCode());
        }
        if(!StringUtils.isEmpty(hospitalQueryVo.getCityCode())){
            hospital.setCityCode(hospitalQueryVo.getCityCode());
        }
        if(!StringUtils.isEmpty(hospitalQueryVo.getHostype())){
            hospital.setDistrictCode(hospitalQueryVo.getHostype());
        }
        if(!StringUtils.isEmpty(hospitalQueryVo.getDistrictCode())){
            hospital.setDistrictCode(hospitalQueryVo.getDistrictCode());
        }
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                //.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withMatcher("hosname", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        Example<Hospital> of = Example.of(hospital,matcher);


        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize, Sort.by("createTime").ascending());

        Page<Hospital> page = hospitalRepository.findAll(of, pageRequest);


        page.getContent().stream().forEach(item->{
            this.packageHospital(item);
        });

        return page;
    }


    @Override
    public void updateStatus(String id, Integer status) {
        Hospital hospital = hospitalRepository.findById(id).get();
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    @Override
    public Hospital detail(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        this.packageHospital(hospital);
        return hospital;
    }

    @Override
    public List<Hospital> findByNameLike(String name) {
        return hospitalRepository.findHospitalByHosnameLike(name);
    }

    @Override
    public Hospital getHospitalDetail(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        this.packageHospital(hospital);
        return hospital;
    }

    private void packageHospital(Hospital hospital){
        String hostype = hospital.getHostype();

        String provinceCode = hospital.getProvinceCode();
        String cityCode = hospital.getCityCode();
        String districtCode = hospital.getDistrictCode();


        String provinceAddress = dictFeignClient.getNameByValue(Long.parseLong(provinceCode));
        String cityAddress = dictFeignClient.getNameByValue(Long.parseLong(cityCode));
        String districtAddress = dictFeignClient.getNameByValue(Long.parseLong(districtCode));

        String level = dictFeignClient.getNameByDictCodeAndValue(DictEnum.HOSTYPE.getDictCode(), Long.parseLong(hostype));

        hospital.getParam().put("hostypeString", level);
        hospital.getParam().put("fullAddress", provinceAddress+cityAddress+districtAddress + hospital.getAddress());

    }
}
