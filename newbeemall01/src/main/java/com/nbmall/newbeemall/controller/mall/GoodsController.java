package com.nbmall.newbeemall.controller.mall;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.NewBeeMallException;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallGoodsDetailVO;
import com.nbmall.newbeemall.controller.vo.SearchPageCategoryVO;
import com.nbmall.newbeemall.entity.NewBeeMallGoods;
import com.nbmall.newbeemall.service.NewBeeMallCategoryService;
import com.nbmall.newbeemall.service.NewBeeMallGoodsService;
import com.nbmall.newbeemall.util.BeanUtil;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Autowired
    private NewBeeMallGoodsService goodsService;

    @Autowired
    private NewBeeMallCategoryService categoryService;


    /*
    * 搜索页面
    * */
    @GetMapping({"/search","search.html"})
    public String search(@RequestParam Map<String,Object> params, HttpServletRequest request){
        if (StringUtils.isEmpty(params.get("page"))){
            params.put("page",1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);

        //封装分类数据
        if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId")+"")){
            Long goodsCategoryId = Long.valueOf(params.get("goodsCategoryId")+"");
            SearchPageCategoryVO searchPageCategoryVO = categoryService.getCategoriesForSearch(goodsCategoryId);
            if (searchPageCategoryVO != null){
                request.setAttribute("goodsCategoryId",goodsCategoryId);
                request.setAttribute("searchPageCategoryVO",searchPageCategoryVO);
            }
        }

        //封装参数供前端回显
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy")+"")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }

        String keyword = "";
        //对keyword去除多余空格
        if ( params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword")+"").trim())){
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword",keyword);
        params.put("keyword",keyword);
        //搜索上架状态下的商品
        params.put("goodsSellStatus",Constants.SELL_STATUS_UP);
        //封装商品数据
        PageQueryUtil queryUtil = new PageQueryUtil(params);
        PageResult pageResult =  goodsService.searchGoods(queryUtil);
        request.setAttribute("pageResult",pageResult);
        return "mall/search";
    }


    /*
    * 商品详情
    * */
    @GetMapping("/goods/detail/{goodsId}")
    public String goodsDetail(@PathVariable("goodsId") Long goodsId,HttpServletRequest request){
        if (goodsId < 1){
            return "error/error_5xx";
        }
        NewBeeMallGoods goods = goodsService.getGoodsBySelect(goodsId);
        if (goods == null){
            NewBeeMallException.fail(ServiceResuleEnum.GOODS_NOT_EXIST.getResult());
        }
        if (goods.getGoodsSellStatus() == Constants.SELL_STATUS_DOWN) {
            NewBeeMallException.fail(ServiceResuleEnum.GOODS_PUT_DOWN.getResult());
        }
        NewBeeMallGoodsDetailVO goodsDetailVO = new NewBeeMallGoodsDetailVO();
        BeanUtil.copyProperties(goods,goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail",goodsDetailVO);
        return "mall/detail";
    }

}
