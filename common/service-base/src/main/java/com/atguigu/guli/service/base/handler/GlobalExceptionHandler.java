package com.atguigu.guli.service.base.handler;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author eternity
 * @create 2019-11-20 20:13
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {//统一异常处理器

    @ExceptionHandler(Exception.class)//处理所有异常
    @ResponseBody
    public R error(Exception e){
//        e.printStackTrace();
        //log.error(e.getMessage());
        log.error(ResultCodeEnum.JSON_PARSE_ERROR.toString());
        log.error(ExceptionUtils.getMessage(e));
        return R.error();
    }

    @ExceptionHandler(BadSqlGrammarException.class)//处理出现实例的异常
    @ResponseBody
    public R error(BadSqlGrammarException e){
        e.printStackTrace();
        return R.setResult(ResultCodeEnum.BAD_SQL_GRAMMAR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)//处理出现实例的异常
    @ResponseBody
    public R error(HttpMessageNotReadableException e){
        e.printStackTrace();
        return R.setResult(ResultCodeEnum.JSON_PARSE_ERROR);
    }
}
