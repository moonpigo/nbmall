package com.nbmall.newbeemall.controller.mall;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.NewBeeMallException;
import com.nbmall.newbeemall.common.NewBeeMallOrderStatusEnum;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallOrderDetailVO;
import com.nbmall.newbeemall.controller.vo.NewBeeMallShoppingCartItemVO;
import com.nbmall.newbeemall.controller.vo.NewBeeMallUserVO;
import com.nbmall.newbeemall.entity.NewBeeMallOrder;
import com.nbmall.newbeemall.service.NewBeeMallOrderService;
import com.nbmall.newbeemall.service.NewBeeMallShoppingCartService;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    private NewBeeMallOrderService orderService;

    @Autowired
    private NewBeeMallShoppingCartService shoppingCartService;

    //保存订单和订单条目
    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<NewBeeMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)){
            //购物车没有条目则跳转至错误页
            NewBeeMallException.fail(ServiceResuleEnum.SHOPPING_ITEM_ERROR.getResult());
        }

        if (StringUtils.isEmpty(user.getAddress())){
            //用户没有填写地址则跳转至错误页
            NewBeeMallException.fail(ServiceResuleEnum.NULL_ADDRESS_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = orderService.saveOrder(user,myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/"+saveOrderResult;
    }

    //得到页面订单，转发到订单详情页面把数据显示出来
    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(@PathVariable("orderNo") String orderNo, HttpSession session, HttpServletRequest request){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        NewBeeMallOrderDetailVO orderDetailVO = orderService.getOrderDetailByOrderNo(orderNo,user.getUserId());
        if (orderDetailVO == null){
            return "error/error_5xx";
        }
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }


    //跳转至支付类型
    @GetMapping("/selectPayType")
    public String selectPayType(@RequestParam("orderNo") String  orderNo,HttpServletRequest request,HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        NewBeeMallOrder order = orderService.getOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo",orderNo);
        request.setAttribute("totalPrice",order.getTotalPrice());
        return "mall/pay-select";
    }

    //跳转至 相应的支付页面
    @GetMapping("/payPage")
    public String payPage(@RequestParam("orderNo") String orderNo,@RequestParam("payType") int payType,
                          HttpSession session,HttpServletRequest request){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        NewBeeMallOrder order = orderService.getOrderByOrderNo(orderNo);
        //todo 判断订单userId
        //todo 判断订单状态
        request.setAttribute("orderNo",orderNo);
        request.setAttribute("totalPrice",order.getTotalPrice());
        if (payType == 1){
            return "mall/alipay";
        }else {
            return "mall/wxpay";
        }
    }

    //支付成功
    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo,@RequestParam("payType")int payType){
        String payResult = orderService.paySuccess(orderNo,payType);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.getSuccessResult();
        } else {
            return ResultGenerator.getFailResult(payResult);
        }
    }


    //我的订单列表页面
    @GetMapping("/orders")
    public  String orderListPage(@RequestParam Map<String,Object> params, HttpServletRequest request, HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId",user.getUserId());
        if (StringUtils.isEmpty(params.get("page"))){
            params.put("page",1);
        }
        params.put("limit",Constants.ORDER_SEARCH_PAGE_LIMIT);
        PageQueryUtil queryUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult",orderService.getMyOrders(queryUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }


    //取消订单
    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo")String orderNo, HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String result = orderService.cancelOrder(orderNo,user.getUserId());
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.getSuccessResult();
        }
        return ResultGenerator.getFailResult(result);
    }

    //确认收货
    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo")String orderNo, HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String result = orderService.finishOrder(orderNo,user.getUserId());
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)){
            return ResultGenerator.getSuccessResult();
        }
        return ResultGenerator.getFailResult(result);
    }
}
