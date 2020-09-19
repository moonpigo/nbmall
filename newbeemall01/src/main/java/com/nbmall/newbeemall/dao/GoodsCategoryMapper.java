package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.GoodsCategory;
import com.nbmall.newbeemall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCategoryMapper {
    List<GoodsCategory> findGoodsCategoryList(PageQueryUtil pageQueryUtil);

    int getTotalGoodsCategories(PageQueryUtil pageQueryUtil);

    GoodsCategory selectByLevelAndName(@Param("categoryLevel") Byte categoryLevel, @Param("categoryName") String categoryName);

    int insertSelective(GoodsCategory goodsCategory);

    GoodsCategory selectByPrimaryKey(@Param("categoryId") Long categoryId);

    int updateByPrimaryKeySelective(GoodsCategory goodsCategory);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(@Param("parentIds") List<Long> parentIds, @Param("categoryLevel") int categoryLevel, @Param("number") int number);

}

