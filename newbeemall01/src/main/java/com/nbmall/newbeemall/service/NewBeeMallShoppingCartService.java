package com.nbmall.newbeemall.service;

import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallShoppingCartItemVO;
import com.nbmall.newbeemall.entity.NewBeeMallShoppingCartItem;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface NewBeeMallShoppingCartService {
    String saveNewBeeMallCartItem(NewBeeMallShoppingCartItem shoppingCartItem);

    List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long userId);

    String updateNewBeeMallCartItem(NewBeeMallShoppingCartItem shoppingCartItem);

    Boolean deleteById(Long shoppingCartItemId);
}
