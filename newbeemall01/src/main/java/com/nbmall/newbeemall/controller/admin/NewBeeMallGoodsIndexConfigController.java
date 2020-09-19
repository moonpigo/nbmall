package com.nbmall.newbeemall.controller.admin;

import com.nbmall.newbeemall.common.IndexConfigTypeEnum;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.entity.IndexConfig;
import com.nbmall.newbeemall.service.NewBeeMallIndexConfigService;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsIndexConfigController {

    @Autowired
    private NewBeeMallIndexConfigService indexConfigService;

    @GetMapping("/indexConfigs")
    public String indexConfig(HttpServletRequest request, @RequestParam("configType")int configType){
        IndexConfigTypeEnum indexConfigTypeEnum = IndexConfigTypeEnum.getIndexConfigTypeEnum(configType);
        if (indexConfigTypeEnum.equals(IndexConfigTypeEnum.DEFAULT)){
            return "error/error_5xx";
        }
        request.setAttribute("path",indexConfigTypeEnum.getName());
        request.setAttribute("configType",configType);
        return "admin/newbee_mall_index_config";
    }

    /*
    列表
    * */
    @ResponseBody
    @GetMapping("/indexConfigs/list")
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.getFailResult("参数异常！");
        }
        PageQueryUtil queryUtil = new PageQueryUtil(params);
        return ResultGenerator.getSuccessResult(indexConfigService.getIndexConfigPages(queryUtil));
    }


    /*
    * 保存
    * */
    @ResponseBody
    @PostMapping("/indexConfigs/save")
    public Result save(@RequestBody IndexConfig indexConfig){
        if(StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigType())
                || Objects.isNull(indexConfig.getConfigRank())
                || StringUtils.isEmpty(indexConfig.getRedirectUrl())
                || Objects.isNull(indexConfig.getGoodsId())){
            return ResultGenerator.getFailResult("参数异常!");
        }
        String result = indexConfigService.saveIndexConfig(indexConfig);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.getSuccessResult();
        }else {
            return ResultGenerator.getFailResult(result);
        }
    }


    /**
     * 详情
     */
    @ResponseBody
    @GetMapping("/indexConfigs/info/{id}")
    public Result info(@PathVariable("id") Long id){
        IndexConfig indexConfig = indexConfigService.getConfigByid(id);
        if (indexConfig == null){
            return ResultGenerator.getFailResult(ServiceResuleEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.getSuccessResult(indexConfig);
    }


    /*
     * 更改
     * */
    @ResponseBody
    @PostMapping("/indexConfigs/update")
    public Result update(@RequestBody IndexConfig indexConfig){
        if(StringUtils.isEmpty(indexConfig.getConfigName())
                || Objects.isNull(indexConfig.getConfigId())
                || Objects.isNull(indexConfig.getConfigType())
                || Objects.isNull(indexConfig.getConfigRank())
                || StringUtils.isEmpty(indexConfig.getRedirectUrl())
                || Objects.isNull(indexConfig.getGoodsId())){
            return ResultGenerator.getFailResult("参数异常!");
        }
        String result = indexConfigService.updateIndexConfig(indexConfig);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.getSuccessResult();
        }else {
            return ResultGenerator.getFailResult(result);
        }
    }

    /*
     * 删除
     * */
    @ResponseBody
    @PostMapping("/indexConfigs/delete")
    public Result delete(@RequestBody Integer[] ids){
        if (ids.length < 1){
            return ResultGenerator.getFailResult("参数异常!");
        }
        if (indexConfigService.deleteBatch(ids)){
            return ResultGenerator.getSuccessResult();
        }
        return ResultGenerator.getFailResult("删除失败");
    }

}
