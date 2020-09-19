package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.NewBeeMallOrder;
import com.nbmall.newbeemall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewBeeMallOrderMapper {

    int insertSelective(NewBeeMallOrder newBeeMallOrder);

    NewBeeMallOrder selectOrderByOrderNo(String orderNo);

    int closeOrder(@Param("orderIds") List<Long> orderIds,@Param("orderStatus") int orderStatus);

    int updateByPrimaryKeySelective(NewBeeMallOrder order);

    int getTotalOrders(PageQueryUtil queryUtil);

    List<NewBeeMallOrder> findOrderList(PageQueryUtil queryUtil);

    NewBeeMallOrder selectByPrimaryKey(Long orderId);

    List<NewBeeMallOrder> selectByPrimaryKeys(List<Long> ids);

    int checkDone(@Param("ids") List<Long> ids);

    int checkOut(@Param("ids") List<Long> ids);

}
