package com.atguigu.yygh.user.controller.admin;


import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.user.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-07-27
 */
@RestController
@Api(tags = "医院设置信息")
@RequestMapping("admin/hosp/hospitalSet")
public class HospitalSetController {
    @Autowired
    private HospitalSetService hospitalSetService;
    @ApiOperation(value = "分页查询医院设置信息")
    @PostMapping("/page/{pageNum}/{pageSize}")
    public R getPageInfo(@ApiParam(name = "pageNum",value = "当前页") @PathVariable Integer pageNum,
                         @ApiParam(name = "pageSize",value = "每页显示多少条")@PathVariable Integer pageSize,
                          @RequestBody HospitalSetQueryVo hospitalSetQueryVo){
        Page<HospitalSet> page = new Page<HospitalSet>(pageNum,pageSize);//参数一是当前页，参数二是每页记录数
        QueryWrapper<HospitalSet> queryWrapper= new QueryWrapper<>();//条件构造器
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())){
            queryWrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())){
            queryWrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        hospitalSetService.page(page,queryWrapper);
        return R.ok().data("rows",page.getRecords()).data("total",page.getTotal());
    }
    @ApiOperation(value = "根据id修改医院设置的状态")
    @PutMapping("/status/{id}/{status}")
    public R updateStatus(@PathVariable Long id,@PathVariable Integer status){
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
//        HospitalSet byId = hospitalSetService.getById(id);
//        byId.setStatus(status);
//        hospitalSetService.updateById(byId);
    }
    @ApiOperation(value = "根据id批量删除医院设置信息")
    @DeleteMapping("/delete")
    public R batchDelete(@RequestBody List<Integer> ids){
        boolean b = hospitalSetService.removeByIds(ids);
        return R.ok();
    }
    @ApiOperation(value = "根据id获取医院设置信息")
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Integer id){
        return R.ok().data("item",hospitalSetService.getById(id));
    }
    @ApiOperation(value = "根据id修改医院设置信息")
    @PutMapping("/update")
    public R updateById(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    @ApiOperation(value = "新增接口")
    @PostMapping ("/save")
    public R save(@RequestBody HospitalSet hospitalSet){
        hospitalSet.setStatus(1);
        //签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean save = hospitalSetService.save(hospitalSet);
        return R.ok();
    }


    @ApiOperation(value = "获取所有医院设置信息")
    @RequestMapping("/findAll")
    public R findAll(){

        try {
            List<HospitalSet> list = hospitalSetService.list();
            return R.ok().data("items",list);
        } catch (Exception e) {
            throw new yyghException(20044,"数字异常");
        }
    }
    @ApiOperation(value = "逻辑删除医院设置信息")
    @DeleteMapping("/deleteById/{id}")
    public R deleteById(@PathVariable Integer id){
        boolean b = hospitalSetService.removeById(id);
        return R.ok();
    }
}

