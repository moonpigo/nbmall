package com.nbmall.newbeemall.controller.mall;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.IndexConfigTypeEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexCarouselVO;
import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexCategoryVO;
import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import com.nbmall.newbeemall.service.NewBeeMallCarouselService;
import com.nbmall.newbeemall.service.NewBeeMallCategoryService;
import com.nbmall.newbeemall.service.NewBeeMallIndexConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private NewBeeMallCarouselService carouselService;

    @Autowired
    private NewBeeMallCategoryService categoryService;

    @Autowired
    private NewBeeMallIndexConfigService indexConfigService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request){
        List<NewBeeMallIndexCategoryVO> categories = categoryService.getCategoryForIndex();
        if (CollectionUtils.isEmpty(categories)){
            return "error/error_5xx";
        }
        List<NewBeeMallIndexCarouselVO> carousels = carouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<NewBeeMallIndexConfigGoodsVO> hotGoodses =  indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(),Constants.INDEX_GOODS_HOT_NUMBER);
        List<NewBeeMallIndexConfigGoodsVO> newGoodses =  indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(),Constants.INDEX_GOODS_NEW_NUMBER);
        List<NewBeeMallIndexConfigGoodsVO> recommendGoodses =  indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(),Constants.INDEX_GOODS_RECOMMOND_NUMBER);

        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品

        return "mall/index";
    }
}
