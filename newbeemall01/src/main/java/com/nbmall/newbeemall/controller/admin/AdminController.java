package com.nbmall.newbeemall.controller.admin;

import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.entity.AdminUser;
import com.nbmall.newbeemall.service.AdminUserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminUserService adminUserService;

    @GetMapping({"/login"})
    public String login(){
        return "admin/login";
    }

    //登录方法
    @PostMapping(value = {"/login"})
    public String login(@RequestParam("userName") String userName,
                      @RequestParam("password") String password,
                      @RequestParam("verifyCode") String verifyCode,
                      HttpSession session){
        if (StringUtils.isEmpty(verifyCode)){
            session.setAttribute("errorMsg","验证码不能为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            session.setAttribute("errorMsg","用户名或者密码不能为空");
            return "admin/login";
        }
        String kaptchaCode = (String)session.getAttribute("verifyCode");
        if (!kaptchaCode.equals(verifyCode)){
            session.setAttribute("errorMsg","验证码错误");
            return "admin/login";
        }

        AdminUser adminUser = adminUserService.login(userName,password);
        if (adminUser != null){
            //保存admin的昵称和id
            session.setAttribute("loginUser",adminUser.getNickName());
            session.setAttribute("loginUserId",adminUser.getAdminUserId());
            return "redirect:/admin/index"; //？
        }else {
            session.setAttribute("errorMsg","登陆失败，请联系作者获得测试账号");
            return "admin/login";
        }
    }

    @GetMapping({"","/","/index","index.html"}) //??
    public String index(HttpServletRequest request){
        request.setAttribute("path","index");
        return "admin/index";
    }


    @GetMapping("/profile")
    public String profile(HttpServletRequest request){
        Integer loginUserId = (int)request.getSession().getAttribute("loginUserId");
        AdminUser adminUser =adminUserService.getUserDetailById(loginUserId);
        if( null == adminUser){
          return "admin/login";
        }
        request.setAttribute("path","profile");
        request.setAttribute("loginUserName",adminUser.getAdminUserName());
        request.setAttribute("nickName",adminUser.getNickName());
        //session过期时间设置为7200秒 即两小时
        //session.setMaxInactiveInterval(60 * 60 * 2);
        return  "admin/profile";
    }

    //修改名称
    @ResponseBody
    @PostMapping("/profile/name")
    public String updateName(HttpServletRequest request,@RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName){
        if (StringUtils.isEmpty(loginUserName) || StringUtils.isEmpty(nickName)){
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updateName(loginUserId,loginUserName,nickName)){
            //新名称修改成功则返回success
            return ServiceResuleEnum.SUCCESS.getResult();
        }else {
            return "修改失败";
        }
    }

    //修改密码
    @ResponseBody
    @PostMapping("/profile/password")
    public String updatePassword(HttpServletRequest request,@RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword){
        if (StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)) {
            return "参数不能为空";
        }
        Integer loginUserId = (int) request.getSession().getAttribute("loginUserId");
        if (adminUserService.updatePassword(loginUserId,originalPassword,newPassword)){
            //新密码修改成功则清空session，前端页面重新跳转到login页面，输入新密码
            request.getSession().removeAttribute("loginUserName");
            request.getSession().removeAttribute("nickName");
            request.getSession().removeAttribute("errorMsg");
            // 返回success
            return ServiceResuleEnum.SUCCESS.getResult();
        }else {
            return "修改失败";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute("loginUserName");
        request.getSession().removeAttribute("nickName");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }

}
