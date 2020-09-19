package com.nbmall.newbeemall.service.impl;

import com.nbmall.newbeemall.common.*;
import com.nbmall.newbeemall.controller.vo.*;
import com.nbmall.newbeemall.dao.NewBeeMallGoodsMapper;
import com.nbmall.newbeemall.dao.NewBeeMallOrderItemMapper;
import com.nbmall.newbeemall.dao.NewBeeMallOrderMapper;
import com.nbmall.newbeemall.dao.NewBeeMallShoppingCartItemMapper;
import com.nbmall.newbeemall.entity.NewBeeMallGoods;
import com.nbmall.newbeemall.entity.NewBeeMallOrder;
import com.nbmall.newbeemall.entity.NewBeeMallOrderItem;
import com.nbmall.newbeemall.entity.StockNumDTO;
import com.nbmall.newbeemall.service.NewBeeMallOrderService;
import com.nbmall.newbeemall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.sql.Time;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class NewBeeMallOrderServiceImpl implements NewBeeMallOrderService {

    @Autowired
    private NewBeeMallShoppingCartItemMapper shoppingCartItemMapper;
    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;
    @Autowired
    private NewBeeMallOrderMapper orderMapper;
    @Autowired
    private NewBeeMallOrderItemMapper orderItemMapper;

    @Override
    public String saveOrder(NewBeeMallUserVO user, List<NewBeeMallShoppingCartItemVO> myShoppingCartItems) {

        List<Long> itemIds = myShoppingCartItems.stream().map(NewBeeMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(NewBeeMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<NewBeeMallGoods> goodsList = goodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否有销售状态为下架的商品
        List<NewBeeMallGoods> downGoods = goodsList.stream()
                .filter(NewBeeMallGoodsTemp -> NewBeeMallGoodsTemp.getGoodsSellStatus() == Constants.SELL_STATUS_DOWN)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(downGoods)){
            //有则转错误页面（错误消息：商品已下架,不能生成订单！）
            NewBeeMallException.fail(downGoods.get(0).getGoodsName()+ ServiceResuleEnum.GOODS_PUT_DOWN_NO_SELLING);
        }

        Map<Long, NewBeeMallGoods> newBeeMallGoodsMap =goodsList.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(),(entity1, entity2) -> entity1));
        //判断购物车每条条目商品
        for (NewBeeMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems){
            //根据购物车中条目的商品id在数据库中查找对应的商品，但数据库不一定存在，所以此处遍历检查，不存在则直接返回错误提醒
            if (!newBeeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())){
                NewBeeMallException.fail(ServiceResuleEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > newBeeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()){
                NewBeeMallException.fail(ServiceResuleEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }

        if (!CollectionUtils.isEmpty(myShoppingCartItems) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(goodsList)){
            if (shoppingCartItemMapper.deleteBatch(itemIds) >0){
                //根据购物车条目Id批量删除购物条目，成功后
                List<StockNumDTO>  stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                //更改商品存储量
                if (goodsMapper.updateStockNum(stockNumDTOS) < 1){
                    NewBeeMallException.fail(ServiceResuleEnum.SHOPPING_ITEM_ERROR.getResult());
                }
                //生成订单
                NewBeeMallOrder newBeeMallOrder = new NewBeeMallOrder();
                //生成并保存订单号
                String orderNo = NumberUtil.getOrderNo();
                newBeeMallOrder.setOrderNo(orderNo);
                newBeeMallOrder.setUserId(user.getUserId());
                newBeeMallOrder.setUserAddress(user.getAddress());
                //保存价格
                int totalPrice = 0;
                for (NewBeeMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
                    totalPrice += shoppingCartItemVO.getGoodsCount()*shoppingCartItemVO.getSellingPrice();
                }
                if (totalPrice < 1) {
                    NewBeeMallException.fail(ServiceResuleEnum.ORDER_PRICE_ERROR.getResult());
                }
                newBeeMallOrder.setTotalPrice(totalPrice);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                newBeeMallOrder.setExtraInfo(extraInfo);
                //保存订单记录
                if (orderMapper.insertSelective(newBeeMallOrder) > 0){
                    List<NewBeeMallOrderItem> orderItems = new ArrayList<>();
                    //遍历购物车条目，使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                    //两者基本上是一样的
                    for (NewBeeMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
                        NewBeeMallOrderItem newBeeMallOrderItem = new NewBeeMallOrderItem();
                        BeanUtil.copyProperties(shoppingCartItemVO,newBeeMallOrderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以返回到实体类中
                        newBeeMallOrderItem.setOrderId(newBeeMallOrder.getOrderId());
                        orderItems.add(newBeeMallOrderItem);
                    }
                    //保存订单条目到数据库
                    if (orderItemMapper.insertBatch(orderItems) >0){
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    NewBeeMallException.fail(ServiceResuleEnum.DB_ERROR.getResult());
                }
                NewBeeMallException.fail(ServiceResuleEnum.DB_ERROR.getResult());
            }
            NewBeeMallException.fail(ServiceResuleEnum.DB_ERROR.getResult());
        }
        NewBeeMallException.fail(ServiceResuleEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResuleEnum.SHOPPING_ITEM_ERROR.getResult();
    }


    @Override
    public NewBeeMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        //根据订单号得到订单
        NewBeeMallOrder newBeeMallOrder = orderMapper.selectOrderByOrderNo(orderNo);

        if (newBeeMallOrder != null){
            //根据订单id得到当前用户的所有订单条目
            List<NewBeeMallOrderItem> orderItems =  orderItemMapper.selectItemByOrderId(newBeeMallOrder.getOrderId());
            if (orderItems != null){
                //将订单条目的属性 复制给 页面订单条目
                List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, NewBeeMallOrderItemVO.class);

                //将订单的属性复制给 页面订单
                NewBeeMallOrderDetailVO orderDetailVO = new NewBeeMallOrderDetailVO();
                BeanUtil.copyProperties(newBeeMallOrder,orderDetailVO);
                orderDetailVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(orderDetailVO.getOrderStatus()).getName());
                orderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(orderDetailVO.getPayType()).getName());
                //封装所有的页面订单条目 给页面订单，并返回
                orderDetailVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                return  orderDetailVO;
            }
        }
        return null;
    }

    @Override
    public NewBeeMallOrder getOrderByOrderNo(String orderNo) {
        return orderMapper.selectOrderByOrderNo(orderNo);
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        NewBeeMallOrder order = orderMapper.selectOrderByOrderNo(orderNo);
        if (order != null){
            //todo 订单状态判断 非待支付状态下不进行修改操作
            if (order.getOrderStatus() != (byte)NewBeeMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()){
                return ServiceResuleEnum.ORDER_NOT_PRE_PAY.getResult();
            }
            order.setOrderStatus((byte)NewBeeMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            order.setPayType((byte)payType);
            order.setPayStatus((byte)PayStatusEnum.PAY_SUCCESS.getPayStatus());
            order.setPayTime(new Date());
            order.setUpdateTime(new Date());

            if (orderMapper.updateByPrimaryKeySelective(order) >0){
                return ServiceResuleEnum.SUCCESS.getResult();
            }else {
                return ServiceResuleEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResuleEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil queryUtil) {
        //按要求找到所有订单和订单总数
        int total = orderMapper.getTotalOrders(queryUtil);
        List<NewBeeMallOrder> orders = orderMapper.findOrderList(queryUtil);
        List<NewBeeMallOrderListVO> orderListVOS =  new ArrayList<>();

        if (total >0){
            //将所有订单的列表对象 复制给页面订单列表对象
            orderListVOS = BeanUtil.copyList(orders, NewBeeMallOrderListVO.class);
            //得到所有订单Id
            List<Long> orderIds = orderListVOS.stream().map(NewBeeMallOrderListVO::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)){
                //根据所有订单Id找到 所有订单条目
                List<NewBeeMallOrderItem> orderItems = orderItemMapper.selectItemByOrderIds(orderIds);
                Map<Long, List<NewBeeMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(NewBeeMallOrderItem::getOrderId));

                //遍历所有订单
                for (NewBeeMallOrderListVO orderListVO: orderListVOS){

                    //转换订单状态类型为中文
                    orderListVO.setOrderStatusString(NewBeeMallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(orderListVO.getOrderStatus()).getName());
                    if (itemByOrderIdMap.containsKey(orderListVO.getOrderId())){
                        //将所有订单条目的列表对象 复制到页面订单条目列表对象
                        List<NewBeeMallOrderItemVO> orderItemVOS = BeanUtil.copyList(itemByOrderIdMap.get(orderListVO.getOrderId()),NewBeeMallOrderItemVO.class);
                        //订单保存订单条目
                        orderListVO.setNewBeeMallOrderItemVOS(orderItemVOS);

                    }
                }
            }

        }
        return new PageResult(orderListVOS,total,queryUtil.getLimit(),queryUtil.getPage());
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        //根据订单号得到订单
        NewBeeMallOrder order = orderMapper.selectOrderByOrderNo(orderNo);
        if (order != null){
            //todo 验证是否是当前userId下的订单，否则报错
            if (order.getUserId() != userId){
                //订单不属于该用户
                return ServiceResuleEnum.ORDER_ERROR.getResult();
            }
            //更改订单状态和更改时间
            if (orderMapper.closeOrder(Collections.singletonList(order.getOrderId()),NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) >0){
                return ServiceResuleEnum.SUCCESS.getResult();
            }else {
                return ServiceResuleEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResuleEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        //根据订单号得到订单
        NewBeeMallOrder order = orderMapper.selectOrderByOrderNo(orderNo);

        if (order != null){
            //todo 验证是否是当前userId下的订单，否则报错
            if (order.getUserId() != userId){
                //订单不属于该用户
                return ServiceResuleEnum.ORDER_ERROR.getResult();
            }
            order.setOrderStatus((byte) NewBeeMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            order.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(order) > 0){
                return ServiceResuleEnum.SUCCESS.getResult();
            }else {
                return ServiceResuleEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResuleEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public PageResult getOrdersPage(PageQueryUtil queryUtil) {
        List<NewBeeMallOrder> orderList = orderMapper.findOrderList(queryUtil);
        int total = orderMapper.getTotalOrders(queryUtil);
        return new PageResult(orderList,total,queryUtil.getLimit(),queryUtil.getPage());
    }

    @Override
    public List<NewBeeMallOrderItemVO> getOrderItem(Long orderId) {
        NewBeeMallOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order != null){
            List<NewBeeMallOrderItem> tempOrderItems = orderItemMapper.selectItemByOrderId(order.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(tempOrderItems)){
                List<NewBeeMallOrderItemVO> orderItems = BeanUtil.copyList(tempOrderItems, NewBeeMallOrderItemVO.class);
                return orderItems;
            }
        }
        return null;
    }

    @Override
    public String updateOrderInfo(NewBeeMallOrder newBeeMallOrder) {
        NewBeeMallOrder dbOrder = orderMapper.selectByPrimaryKey(newBeeMallOrder.getOrderId());
        //不问空，且订单状态为待支付、未支付、配货才允许修改
        if (dbOrder != null && dbOrder.getOrderStatus() >= 0 && dbOrder.getOrderStatus() <3){
            dbOrder.setUserAddress(newBeeMallOrder.getUserAddress());
            dbOrder.setTotalPrice(newBeeMallOrder.getTotalPrice());
            dbOrder.setUpdateTime(new Date());
            if (orderMapper.updateByPrimaryKeySelective(dbOrder) > 0){
                return ServiceResuleEnum.SUCCESS.getResult();
            }
            return ServiceResuleEnum.DB_ERROR.getResult();
        }
        return ServiceResuleEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String checkDone(Long[] ids) {
        List<NewBeeMallOrder> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        if (!CollectionUtils.isEmpty(orders)){
            //收集已删除、不是已支付状态的错误订单
            String errorOrderNos = "";
            for (NewBeeMallOrder order : orders){
                if (order.getIsDeleted() == 1){
                    errorOrderNos += order.getOrderNo() + "";
                    continue;
                }
                if (order.getOrderStatus() != 1){
                    errorOrderNos += order.getOrderNo() + "";
                }
            }

            if (StringUtils.isEmpty(errorOrderNos)){
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (orderMapper.checkDone(Arrays.asList(ids)) >0){
                    return ServiceResuleEnum.SUCCESS.getResult();
                }else {
                    return ServiceResuleEnum.DB_ERROR.getResult();
                }
            }else {
                if (errorOrderNos.length() >0 && errorOrderNos.length() <100){
                    return  "以下订单不是已支付状态，无法执行出库操作： "+ errorOrderNos;
                }else {
                    return  "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        return ServiceResuleEnum.DATA_NOT_EXIST.getResult();
    }

    public String checkOut(Long[] ids) {
        List<NewBeeMallOrder> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        if (!CollectionUtils.isEmpty(orders)){
            //收集已删除、不是已支付和配货完成状态的错误订单
            String errorOrderNos = "";
            for (NewBeeMallOrder order : orders){
                if (order.getIsDeleted() == 1){
                    errorOrderNos += order.getOrderNo() + "";
                    continue;
                }
                if (order.getOrderStatus() != 1 && order.getOrderStatus() != 2){
                    errorOrderNos += order.getOrderNo() + "";
                }
            }

            if (StringUtils.isEmpty(errorOrderNos)){
                //订单状态正常 可以执行出库完成操作 修改订单状态和更新时间
                if (orderMapper.checkOut(Arrays.asList(ids)) >0){
                    return ServiceResuleEnum.SUCCESS.getResult();
                }else {
                    return ServiceResuleEnum.DB_ERROR.getResult();
                }
            }else {
                if (errorOrderNos.length() >0 && errorOrderNos.length() <100){
                    return  "以下订单不是支付成功或配货完成状态的订单，无法执行出库操作： "+ errorOrderNos;
                }else {
                    return  "你选择了太多不是支付成功或配货完成状态的订单，无法执行出库操作";
                }
            }
        }
        return ServiceResuleEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String closeOrder(Long[] ids) {
        List<NewBeeMallOrder> orders = orderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        if (!CollectionUtils.isEmpty(orders)){

            String errorOrderNos = "";
            for (NewBeeMallOrder order : orders){
                //isDeleted=1， 一定是关闭的的订单
                if (order.getIsDeleted() == 1){
                    errorOrderNos += order.getOrderNo() + "";
                    continue;
                }
                //交易成功和已关闭的订单不能再关闭
                if (order.getOrderStatus() == 4 && order.getOrderStatus() <0){
                    errorOrderNos += order.getOrderNo() + "";
                }
            }

            if (StringUtils.isEmpty(errorOrderNos)){
                //订单状态正常 可以执行关闭订单操作 修改订单状态和更新时间
                if (orderMapper.closeOrder(Arrays.asList(ids),NewBeeMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) >0){
                    return ServiceResuleEnum.SUCCESS.getResult();
                }else {
                    return ServiceResuleEnum.DB_ERROR.getResult();
                }
            }else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() >0 && errorOrderNos.length() <100){
                    return  "以下订单不能执行关闭操作： "+ errorOrderNos;
                }else {
                    return  "你选择了太多不能执行关闭操作的订单。";
                }
            }
        }
        return ServiceResuleEnum.DATA_NOT_EXIST.getResult();
    }
}
