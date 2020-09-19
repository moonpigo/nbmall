package com.nbmall.newbeemall.controller.admin;

import com.nbmall.newbeemall.service.NewBeeMallUserService;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class NewBeeMallUserController {

    @Autowired
    private NewBeeMallUserService userService;

    @GetMapping("/user")
    public String user(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_user");
        return "/admin/newbee_mall_user";
    }

    @GetMapping("/users/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty("page") || StringUtils.isEmpty("limit")){
            return ResultGenerator.getFailResult("参数异常！");
        }
        PageQueryUtil queryUtil = new PageQueryUtil(params);
        return ResultGenerator.getSuccessResult(userService.getUserPages(queryUtil));
    }

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     */
    @PostMapping("/users/changeLockStatus/{lockStatus}")
    @ResponseBody
    public Result changeLockStatus(@RequestBody Integer[] ids,@PathVariable("lockStatus") int lockStatus){
        if (ids.length <1){
            return ResultGenerator.getFailResult("参数异常！");
        }
        if (lockStatus != 0 && lockStatus!=1){
            return ResultGenerator.getFailResult("操作非法！");
        }
        if (userService.lockUsers(ids,lockStatus)){
            return ResultGenerator.getSuccessResult();
        }else {
            return ResultGenerator.getFailResult("禁用失败！");
        }

    }
}
