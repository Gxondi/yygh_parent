package com.atguigu.yygh.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.DictListener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-08-11
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    @Cacheable(value = "dict")
    public List<Dict> getChildListByParentId(Long pid) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<Dict>();
        queryWrapper.eq("parent_id",pid);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        for (Dict dict : dicts) {
            dict.setHasChildren(hasChild(dict.getId()));
        }
        return dicts;
    }

    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<Dict> list = baseMapper.selectList(null);
        List<DictEeVo> dictEeVoList = new ArrayList<>(list.size());
        for (Dict dict : list) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVoList.add(dictEeVo);
        }
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), Dict.class).sheet("学生列表1").doWrite(dictEeVoList);
    }

    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), DictEeVo.class,new DictListener(baseMapper)).sheet(0).doRead();

    }

    @Override
    public String getNameByValue(Long value) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<Dict>();
        queryWrapper.eq("value",value);
        Dict dict = baseMapper.selectOne(queryWrapper);
        if(dict != null){
            return dict.getName();
        }
        return null;
    }

    @Override
    public String getNameByDictCodeAndValue(String dictCode,Long value) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<Dict>();
        queryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(queryWrapper);


        QueryWrapper<Dict> queryWrapper2=new QueryWrapper<Dict>();
        queryWrapper2.eq("parent_id", dict.getId());
        queryWrapper2.eq("value", value);

        Dict dict2 = baseMapper.selectOne(queryWrapper2);
        if (dict2 != null) {
            return dict2.getName();
        } else {
            return "三甲医院111";
        }

    }

    private boolean hasChild(Long pid) {
        QueryWrapper<Dict> queryWrapper=new QueryWrapper<Dict>();
        queryWrapper.eq("parent_id",pid);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count >0;
    }
}
