package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.NewBeeMallGoods;
import com.nbmall.newbeemall.entity.StockNumDTO;
import com.nbmall.newbeemall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewBeeMallGoodsMapper {
    List<NewBeeMallGoods> findNewBeeMallGoodsList(PageQueryUtil pageQueryUtil);

    int getTotalNewBeeMallGoods(PageQueryUtil pageQueryUtil);

    int insertSelective(NewBeeMallGoods goods);

    NewBeeMallGoods selectByPramaryKey(@Param("goodsId") Long goodsId);

    int updateByPrimaryKeySelective(NewBeeMallGoods goods);

    int batchUpdateSellStatus(@Param("ids") Long[] ids, @Param("sellSatus") Byte sellStatus);

    List<NewBeeMallGoods> findNewBeeMallGoodsListBySearch(PageQueryUtil queryUtil);

    int getTotalNewBeeMallGoodsBySearch(PageQueryUtil queryUtil);

    List<NewBeeMallGoods> selectByPrimaryKeys(List<Long> gooodsIds);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);
}
