package com.nbmall.newbeemall.controller.admin;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.NewBeeMallCategoryLevelEnum;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.entity.GoodsCategory;
import com.nbmall.newbeemall.entity.NewBeeMallGoods;
import com.nbmall.newbeemall.service.NewBeeMallCategoryService;
import com.nbmall.newbeemall.service.NewBeeMallGoodsService;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsController {

    @Autowired
    private NewBeeMallGoodsService newBeeMallGoodsService;

    @Autowired
    private NewBeeMallCategoryService newBeeMallCategoryService;

    @GetMapping("/goods")
    public String goods(HttpServletRequest request){
        request.setAttribute("path","newbee_mall_goods");
        return "admin/newbee_mall_goods";
    }

    //商品列表
    @GetMapping("/goods/list")
    @ResponseBody
    public Result goodsList(@RequestParam Map<String,Object> params){
        if (Objects.isNull(params.get("page")) || Objects.isNull(params.get("limit"))){
            return ResultGenerator.getFailResult("参数错误！");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult pageResult = newBeeMallGoodsService.getNewBeeMallGoodsPage(pageQueryUtil);
        return ResultGenerator.getSuccessResult(pageResult);
    }

    //新增商品
    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.lEVEL_ONT.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.lEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/newbee_mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    //保存商品
    @PostMapping("/goods/save")
    @ResponseBody
    public Result goodsSave(@RequestBody NewBeeMallGoods goods){
       if (StringUtils.isEmpty(goods.getGoodsName())
               || StringUtils.isEmpty(goods.getGoodsIntro())
               || StringUtils.isEmpty(goods.getTag())
               || Objects.isNull(goods.getOriginalPrice())
               || Objects.isNull(goods.getGoodsCategoryId())
               || Objects.isNull(goods.getSellingPrice())
               || Objects.isNull(goods.getStockNum())
               || Objects.isNull(goods.getGoodsSellStatus())
               || StringUtils.isEmpty(goods.getGoodsCoverImg())
               || StringUtils.isEmpty(goods.getGoodsDetailContent())){
            return ResultGenerator.getFailResult("参数异常！");
       }
       String result = newBeeMallGoodsService.saveNewBeeMallGoods(goods);
       if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
           return ResultGenerator.getSuccessResult();
       }else {
           return ResultGenerator.getFailResult(result);
       }
    }

    /*
    编辑商品
    通过选中的goodsId，找到其商品所属的各级分类，并将各级分类的id保存到request中
    还保存所属一级分类下的二级分类，所属二级分类下的三级分类
     */
    @GetMapping("/goods/edit/{goodsId}")
    public String edit(@PathVariable("goodsId") Long goodsId,HttpServletRequest request){
        request.setAttribute("path","edit");
        NewBeeMallGoods goods = newBeeMallGoodsService.getGoodsBySelect(goodsId);
        if (goods == null){
            return "/error/error_400";
        }

        if (goods.getGoodsCategoryId()>0){
            if (goods.getGoodsCategoryId() != null || goods.getGoodsCategoryId()>0){
//           有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory curreGoodsCategory = newBeeMallCategoryService.getGoodsCategoryById(goods.getGoodsCategoryId());
//                判断该商品所属的分类级别不为空并且是三级分类
                if (curreGoodsCategory != null && curreGoodsCategory.getCategoryLevel()==(NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel())){
//                  查询所有一级分类
                    List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.lEVEL_ONT.getLevel());
//                  查询该商品所在的分类级别的父级分类（即所属二级分类）下的所有三级分类
                    List<GoodsCategory> thirdLevelCategories  = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(curreGoodsCategory.getParentId()),NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel());

//                  查询该商品所在的分类级别的父分类级别（即所属二级分类）
                    GoodsCategory secondLevelCategory = newBeeMallCategoryService.getGoodsCategoryById(curreGoodsCategory.getParentId());
                    if (secondLevelCategory != null){
//                       查询该商品所属二级分类的父级别（即所属一级分类）下的所有二级分类
                        List<GoodsCategory> secondLevelCategories  = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategory.getParentId()),NewBeeMallCategoryLevelEnum.lEVEL_TWO.getLevel());
//                        查询该商品所在的分类级别的父分类级别（即所属二级分类） 的父分类级别（即所属一级分类）
                        GoodsCategory firstLevelCategory = newBeeMallCategoryService.getGoodsCategoryById(secondLevelCategory.getParentId());
                        if(firstLevelCategory != null){
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories",firstLevelCategories);
                            request.setAttribute("secondLevelCategories",secondLevelCategories);
                            request.setAttribute("thirdLevelCategories",thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId",firstLevelCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId",secondLevelCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId",curreGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }

        if (goods.getGoodsCategoryId()== 0){
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.lEVEL_ONT.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.lEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods",goods);
        request.setAttribute("path","goods-edit");
        return "admin/newbee_mall_goods_edit";
    }

    //修改商品
    @ResponseBody
    @PostMapping("/goods/update")
    public Result goodsUpdate(@RequestBody NewBeeMallGoods goods){
        if (StringUtils.isEmpty(goods.getGoodsName())
                || StringUtils.isEmpty(goods.getGoodsIntro())
                || ObjectUtils.isEmpty(goods.getOriginalPrice())
                || ObjectUtils.isEmpty(goods.getSellingPrice())
                || ObjectUtils.isEmpty(goods.getStockNum())
                || StringUtils.isEmpty(goods.getTag())
                || ObjectUtils.isEmpty(goods.getGoodsCategoryId())
                || ObjectUtils.isEmpty(goods.getGoodsSellStatus())
                || StringUtils.isEmpty(goods.getGoodsDetailContent())
                || StringUtils.isEmpty(goods.getGoodsCoverImg())
                || ObjectUtils.isEmpty(goods.getGoodsId())){
            return ResultGenerator.getFailResult("参数异常！");
        }
        String result = newBeeMallGoodsService.updateGoods(goods);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.getSuccessResult();
        }else {
            return ResultGenerator.getFailResult(result);
        }
    }

    @PutMapping("/goods/status/{sellStatus}")
    @ResponseBody
    public Result status(@RequestBody Long[] ids ,@PathVariable("sellStatus") Byte sellStatus){
        if (ids.length < 1){
            return ResultGenerator.getFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus !=Constants.SELL_STATUS_DOWN){
            return ResultGenerator.getFailResult("销售状态异常！");
        }
        if (newBeeMallGoodsService.batchUpdateSellStatus(ids,sellStatus)){
            return ResultGenerator.getSuccessResult();
        }else {
            return ResultGenerator.getFailResult("修改失败");
        }
    }
}
