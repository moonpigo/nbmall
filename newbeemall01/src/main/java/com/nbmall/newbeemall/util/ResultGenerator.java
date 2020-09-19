package com.nbmall.newbeemall.util;

import org.springframework.util.StringUtils;

//响应结果生成工具
public class ResultGenerator {

    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final String DEFAULT_FAIL_MESSAGE = "FAIL";
    private static final int RESULT_CODE_SUCCESS = 200;
    private static final int RESULT_CODE_SERVER_ERROR = 500;

    public static Result getSuccessResult(){
        Result result = new Result();
        result.setCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return result;
    }

    public static Result getSuccessResult(String message){
        Result<Object> result = new Result<>();
        result.setCode(RESULT_CODE_SUCCESS);
        result.setMessage(message);
        return result;
    }

    public static Result getSuccessResult(Object data){
        Result result = new Result();
        result.setCode(RESULT_CODE_SUCCESS);
        result.setMessage(DEFAULT_SUCCESS_MESSAGE);
        result.setData(data);
        return result;
    }

    public static Result getFailResult(String message){
        Result result = new Result();
        result.setCode(RESULT_CODE_SERVER_ERROR);
        if (StringUtils.isEmpty(message)){
            result.setMessage(DEFAULT_FAIL_MESSAGE);
        }else {
        result.setMessage(message);
        }
        return result;
    }

    public static Result getErrorResult(int code,String message){
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
