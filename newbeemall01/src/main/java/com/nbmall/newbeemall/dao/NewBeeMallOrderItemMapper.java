package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.NewBeeMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewBeeMallOrderItemMapper {
    int insertBatch(@Param("orderItems") List<NewBeeMallOrderItem> orderItems);

    List<NewBeeMallOrderItem> selectItemByOrderId(Long orderId);

    List<NewBeeMallOrderItem> selectItemByOrderIds(List<Long> orderIds);
}
