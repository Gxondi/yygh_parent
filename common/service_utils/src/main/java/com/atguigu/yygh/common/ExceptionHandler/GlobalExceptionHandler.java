package com.atguigu.yygh.common.ExceptionHandler;


import com.atguigu.yygh.common.Exception.yyghException;
import com.atguigu.yygh.common.result.R;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice//全局异常处理
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R handlerExceptionHandler(Exception ex){
        ex.printStackTrace();
        return R.ok();
    }
    @ExceptionHandler(yyghException.class)
    @ResponseBody
    public R handleryyghExceptionHandler(yyghException yyex){
        yyex.printStackTrace();
        return R.error().code(20011).message("数字异常");
    }
}
