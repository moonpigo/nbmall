package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.NewBeeMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewBeeMallShoppingCartItemMapper {

    NewBeeMallShoppingCartItem selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId")Long goodsId);


    int selectCountByUserId(Long userId);

    int insertSelective(NewBeeMallShoppingCartItem shoppingCartItem);

    List<NewBeeMallShoppingCartItem> selectCartItemByUserId(Long userId);

    NewBeeMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    int updateByPrimaryKeySelective(NewBeeMallShoppingCartItem shoppingCartItemUpdate);

    int deleteByPrimaryKey(Long shoppingCartItemId);

    int deleteBatch(List<Long> itemIds);
}
