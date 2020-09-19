package com.nbmall.newbeemall.controller.mall;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallUserVO;
import com.nbmall.newbeemall.entity.MallUser;
import com.nbmall.newbeemall.service.NewBeeMallUserService;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PersonalController {

    @Autowired
    NewBeeMallUserService userService;

    @GetMapping({"/login","login.html"})
    public String loginPage(){
        return "mall/login";
    }

    /*
    * 登录
    * */
    @PostMapping("/login")
    @ResponseBody
    public Result login(@RequestParam("loginName")String loginName, @RequestParam("password")String password,
                        @RequestParam("verifyCode") String verifyCode, HttpSession session){
        if (StringUtils.isEmpty(loginName)){
            return ResultGenerator.getFailResult(ServiceResuleEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)){
            return ResultGenerator.getFailResult(ServiceResuleEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)){
            return ResultGenerator.getFailResult(ServiceResuleEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }

        String mallVerifyCode = session.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";
        if (StringUtils.isEmpty(mallVerifyCode) || !mallVerifyCode.equals(verifyCode)){
            return ResultGenerator.getFailResult(ServiceResuleEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }
        String loginResult = userService.login(loginName,password,session);
        //登陆成功
        if (ServiceResuleEnum.SUCCESS.getResult().equals(loginResult)){
            return ResultGenerator.getSuccessResult();
        }
        //登录失败
        return ResultGenerator.getFailResult(loginResult);
    }


    @GetMapping({"/register","register.html"})
    public String registerPage(){
        return "mall/register";
    }

    /*
    * 注册
    * */
    @PostMapping("/register")
    @ResponseBody
    public Result register(@RequestParam("loginName")String loginName, @RequestParam("password")String password,
                        @RequestParam("verifyCode") String verifyCode, HttpSession session){
        if (StringUtils.isEmpty(loginName)){
            return ResultGenerator.getFailResult(ServiceResuleEnum.LOGIN_NAME_NULL.getResult());
        }
        if (StringUtils.isEmpty(password)){
            return ResultGenerator.getFailResult(ServiceResuleEnum.LOGIN_PASSWORD_NULL.getResult());
        }
        if (StringUtils.isEmpty(verifyCode)){
            return ResultGenerator.getFailResult(ServiceResuleEnum.LOGIN_VERIFY_CODE_NULL.getResult());
        }

        String mallVerifyCode = session.getAttribute(Constants.MALL_VERIFY_CODE_KEY) + "";

        if (StringUtils.isEmpty(mallVerifyCode) || !mallVerifyCode.equals(verifyCode)){
            return ResultGenerator.getFailResult(ServiceResuleEnum.LOGIN_VERIFY_CODE_ERROR.getResult());
        }

        String registerResult = userService.register(loginName,password,mallVerifyCode);
        //注册成功
        if (ServiceResuleEnum.SUCCESS.getResult().equals(registerResult)){
            return ResultGenerator.getSuccessResult();
        }
        //注册失败
        return ResultGenerator.getFailResult(registerResult);
    }


    @GetMapping("/personal")
    public String personalPage(HttpServletRequest request) {
        request.setAttribute("path","personal");
        return "mall/personal";
    }

    /*
    *  更改个人信息
    * */
    @PostMapping("/personal/updateInfo")
    @ResponseBody
    public Result updateInfo(@RequestBody MallUser mallUser,HttpSession session){
        NewBeeMallUserVO userVO = userService.updateUserInfo(mallUser,session);
        if (userVO == null){
            return ResultGenerator.getFailResult("修改失败");
        }
        return ResultGenerator.getSuccessResult();
    }

    /*
    * 退出登录
    * */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute(Constants.MALL_USER_SESSION_KEY);
        return "mall/login";
    }
}
