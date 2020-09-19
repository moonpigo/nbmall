package com.nbmall.newbeemall.controller.admin;

import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.entity.Carousel;
import com.nbmall.newbeemall.service.NewBeeMallCarouselService;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class NewBeeMallCarouselController {

    @Autowired
    private NewBeeMallCarouselService carouselService;

    @GetMapping("/carousels")
    public String carousel(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_carousel");
        return "admin/newbee_mall_carousel";
    }

    //列表
    @GetMapping("/carousels/list")
    @ResponseBody
    public Result List(@RequestParam Map<String,Object> params){
        if (params.get("page") == null || params.get("limit")== null){
            return ResultGenerator.getFailResult("参数异常");
        }
        PageQueryUtil queryUtil = new PageQueryUtil(params);
        PageResult successResult = carouselService.getCarouselsPage(queryUtil);
        return ResultGenerator.getSuccessResult(successResult);
    }

    //添加
    @PostMapping("/carousels/save")
    @ResponseBody
    public Result save(@RequestBody Carousel carousel){
        if(StringUtils.isEmpty(carousel.getCarouselUrl())
            || Objects.isNull(carousel.getCarouselRank())){
            return ResultGenerator.getFailResult("参数异常");
        }
        String result = carouselService.saveCarousel(carousel);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.getSuccessResult();
        }else {
            return ResultGenerator.getFailResult(result);
        }
    }

    /*
    编辑，根据id得到carousel对象，并将carousel封装回到编辑的模态框，显示数据出来
    * */
    @GetMapping("/carousels/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Integer id){
        Carousel carousel = carouselService.getCarouselByPrimaryKey(id);
        if (carousel == null){
           return ResultGenerator.getFailResult(ServiceResuleEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.getSuccessResult(carousel);
    }

    //更改
    @PostMapping("/carousels/update")
    @ResponseBody
    public Result update(@RequestBody Carousel carousel){
        if(Objects.isNull(carousel.getCarouselId())
                || StringUtils.isEmpty(carousel.getCarouselUrl())
                || Objects.isNull(carousel.getCarouselRank())){
            return ResultGenerator.getFailResult("参数异常");
        }
        String result = carouselService.updateCarousel(carousel);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.getSuccessResult();
        }else {
            return ResultGenerator.getFailResult(result);
        }
    }

    @PostMapping("/carousels/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if (ids.length<1){
            return ResultGenerator.getFailResult("参数异常！");
        }
        if (carouselService.deleteBatch(ids)){
            return ResultGenerator.getSuccessResult();
        }
        return ResultGenerator.getFailResult("删除失败");
    }

}
