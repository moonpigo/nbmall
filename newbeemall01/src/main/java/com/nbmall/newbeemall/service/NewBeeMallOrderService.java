package com.nbmall.newbeemall.service;

import com.nbmall.newbeemall.controller.vo.*;
import com.nbmall.newbeemall.entity.NewBeeMallOrder;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface NewBeeMallOrderService {
    String saveOrder(NewBeeMallUserVO user, List<NewBeeMallShoppingCartItemVO> myShoppingCartItems);

    NewBeeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    String cancelOrder(String orderNo, Long userId);

    NewBeeMallOrder getOrderByOrderNo(String orderNo);

    String paySuccess(String orderNo, int payType);

    PageResult getMyOrders(PageQueryUtil queryUtil);

    String finishOrder(String orderNo, Long userId);

    PageResult getOrdersPage(PageQueryUtil queryUtil);

    List<NewBeeMallOrderItemVO> getOrderItem(Long orderId);

    String updateOrderInfo(NewBeeMallOrder newBeeMallOrder);

    String checkDone(Long[] ids);

    String checkOut(Long[] ids);

    String closeOrder(Long[] ids);
}
