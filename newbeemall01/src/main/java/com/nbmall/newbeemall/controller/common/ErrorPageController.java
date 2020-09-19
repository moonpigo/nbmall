package com.nbmall.newbeemall.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class ErrorPageController implements ErrorController {

    private static ErrorPageController errorPageController;

    private final static String ERROR_PATH = "/error";

    @Autowired
    private ErrorAttributes errorAttributes;


    public ErrorPageController(ErrorAttributes errorAttributes){
        this.errorAttributes = errorAttributes;
    }

    public ErrorPageController(){
        if (errorPageController == null){
            errorPageController = new ErrorPageController(errorAttributes);
        }
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request){
        HttpStatus status = getStatus(request);
        if (HttpStatus.BAD_GATEWAY == status){
            return new ModelAndView("error/error_400");
        }else if (HttpStatus.NO_CONTENT == status){
            return new ModelAndView("error/error_404");
        }else {
            return new ModelAndView("error/error_5xx");
        }
    }

    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ResponseEntity<Map<String,Object>> error(HttpServletRequest request){
        Map<String,Object> body = getErrorAttributes(request, getTraceParameter(request));
        HttpStatus status = getStatus(request);
        return new ResponseEntity<Map<String,Object>>(body,status);
    }


    public boolean getTraceParameter(HttpServletRequest request){
        String parameter = request.getParameter("trace");
        if (parameter == null){
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }


    public Map<String,Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace){
        ServletWebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }


    public HttpStatus getStatus(HttpServletRequest request){
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if ( statusCode != null){
            try {
                return HttpStatus.valueOf(statusCode);
            }catch (Exception e){

            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
