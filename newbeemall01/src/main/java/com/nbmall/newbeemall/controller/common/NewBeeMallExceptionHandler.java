package com.nbmall.newbeemall.controller.common;

import com.nbmall.newbeemall.common.NewBeeMallException;
import com.nbmall.newbeemall.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * newbee-mall全局异常处理
 */
@RestControllerAdvice
public class NewBeeMallExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e , HttpServletRequest request){
        Result result = new Result();
        result.setCode(500);
        if (e instanceof NewBeeMallException){
            result.setMessage(e.getMessage());
        }else {
            e.printStackTrace();
            result.setMessage("未知异常，请联系管理员");
        }
        String contentTypeHeader = request.getHeader("Content-Type");
        String acceptHeader = request.getHeader("Accept");
        String xRequestWith = request.getHeader("X-Requested-With");
        if ((contentTypeHeader != null && contentTypeHeader.contains("application/json"))
            || (acceptHeader != null && acceptHeader.contains("application/json"))
            || ("XMLHttpRequest".equalsIgnoreCase(xRequestWith))) {
            return result;
        }else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("message",e.getMessage());
            modelAndView.addObject("url",request.getRequestURL());
            modelAndView.addObject("stackTrace",e.getStackTrace());
            return modelAndView;
        }
    }
}
