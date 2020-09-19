package com.nbmall.newbeemall.service;

import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexCategoryVO;
import com.nbmall.newbeemall.controller.vo.SearchPageCategoryVO;
import com.nbmall.newbeemall.entity.GoodsCategory;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.Result;

import java.util.LinkedHashMap;
import java.util.List;

public interface NewBeeMallCategoryService {
    PageResult getCategorisPage(PageQueryUtil pageQueryUtil);

    String saveGategory(GoodsCategory goodsCategory);

    String updateCategory(GoodsCategory goodsCategory);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);

    GoodsCategory getGoodsCategoryById(Long categoryId);

    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);

    List<NewBeeMallIndexCategoryVO> getCategoryForIndex();
}
