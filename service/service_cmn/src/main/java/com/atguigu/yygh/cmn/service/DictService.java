package com.atguigu.yygh.cmn.service;


import com.atguigu.yygh.model.cmn.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-08-11
 */
public interface DictService extends IService<Dict> {

    List<Dict> getChildListByParentId(Long pid);

    void download(HttpServletResponse response) throws IOException;

    void upload(MultipartFile file) throws IOException;

    String getNameByValue(Long dictCode);

    String getNameByDictCodeAndValue(String dictCode, Long value);
}
