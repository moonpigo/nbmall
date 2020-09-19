package com.nbmall.newbeemall.controller.admin;

import com.nbmall.newbeemall.common.NewBeeMallCategoryLevelEnum;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.entity.GoodsCategory;
import com.nbmall.newbeemall.service.NewBeeMallCategoryService;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsCategoryController {

    @Autowired
    private NewBeeMallCategoryService newBeeMallCategoryService;

//    点击分类管理时
    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest request, @RequestParam("parentId") Long parentId,
                                 @RequestParam("categoryLevel") Byte categoryLevel,
                                 @RequestParam("backParentId") Long backParentId){
        if (categoryLevel == null || categoryLevel < 1 || categoryLevel > 3){
            return "error/error_5xx";
        }
        request.setAttribute("path","newbee_mall_category");
        request.setAttribute("parentId",parentId);
        request.setAttribute("categoryLevel",categoryLevel);
        request.setAttribute("backParentId",backParentId);
        return "admin/newbee_mall_category";
    }

    /*
    显示jqGrid表的第一页数据。
    传递数据过来，把(当前页数据，总记录数，每页条数，当前)传递回去
     */
    @ResponseBody
    @RequestMapping(value = "/categories/list", method = RequestMethod.GET)
    public Result list(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) ||StringUtils.isEmpty(params.get("limit")) ||StringUtils.isEmpty(params.get("categoryLevel")) || StringUtils.isEmpty(params.get("parentId"))){
            ResultGenerator.getFailResult("参数异常!");
        }
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        PageResult pageResult = newBeeMallCategoryService.getCategorisPage(pageQueryUtil);
        Result result = ResultGenerator.getSuccessResult(pageResult);
        return result;
    }

//    保存新增的分类
    @ResponseBody
    @PostMapping("/categories/save")
    public Result save(@RequestBody GoodsCategory goodsCategory){

        if (Objects.isNull(goodsCategory.getCategoryLevel()) || StringUtils.isEmpty(goodsCategory.getCategoryName()) ||
             Objects.isNull(goodsCategory.getCategoryRank()) || Objects.isNull(goodsCategory.getParentId())){
            return ResultGenerator.getFailResult("参数出现异常！");
        }
        String result = newBeeMallCategoryService.saveGategory(goodsCategory);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            //当返回success
            return ResultGenerator.getSuccessResult();
        }
        //不成功时封装失败信息
         return ResultGenerator.getFailResult(result);
    }

//  更改分类
    @ResponseBody
    @PostMapping("/categories/update")
    public Result update(@RequestBody GoodsCategory goodsCategory) {
        if (Objects.isNull(goodsCategory.getCategoryId()) || Objects.isNull(goodsCategory.getCategoryLevel()) || StringUtils.isEmpty(goodsCategory.getCategoryName()) ||
                Objects.isNull(goodsCategory.getCategoryRank()) || Objects.isNull(goodsCategory.getParentId())){
            return ResultGenerator.getFailResult("参数出现异常！");
        }
        String result = newBeeMallCategoryService.updateCategory(goodsCategory);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.getSuccessResult();
        }
        return ResultGenerator.getFailResult(result);
    }


    //下拉框根据选择的Id显示下一级分类
    @ResponseBody
    @GetMapping("/categories/categoryByselect")
    public Result categoryByselect(@RequestParam("categoryId") Long categoryId) {
        if (categoryId == null || categoryId < 1) {
            return ResultGenerator.getFailResult("缺少参数");
        }
        GoodsCategory goodsCategory = newBeeMallCategoryService.getGoodsCategoryById(categoryId);
        if (goodsCategory == null || goodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel()) {
            return ResultGenerator.getFailResult("参数异常！");
        }

        Map categoryResult = new HashMap(2);
        //如果是一级分类则返回当前一级分类下的所有二级分类，以及二级分类列表中第一条数据下的所有三级分类列表
        //查询一级分类列表中第一个实体的所有二级分类
        if (goodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.lEVEL_ONT.getLevel()) {
            List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.lEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if (goodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.lEVEL_TWO.getLevel()) {
            //如果是二级分类则返回当前分类下的所有三级分类列表
            List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdLevelCategories);
        }
        return ResultGenerator.getSuccessResult(categoryResult);
    }


}
