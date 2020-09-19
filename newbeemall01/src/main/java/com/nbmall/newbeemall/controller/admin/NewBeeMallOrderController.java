package com.nbmall.newbeemall.controller.admin;

import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallOrderItemVO;
import com.nbmall.newbeemall.entity.NewBeeMallOrder;
import com.nbmall.newbeemall.service.NewBeeMallOrderService;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class NewBeeMallOrderController {

    @Autowired
    private NewBeeMallOrderService orderService;

    @GetMapping("/orders")
    public String orderPage(HttpServletRequest request){
        request.setAttribute("path","orders");
        return "admin/newbee_mall_order";
    }


    //订单列表，返回给jqGrid
    @RequestMapping(value = "/orders/list", method = RequestMethod.GET)
    @ResponseBody
    public Result orderList(@RequestParam Map<String,Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.getFailResult("参数异常！");
        }
        PageQueryUtil queryUtil = new PageQueryUtil(params);
        return ResultGenerator.getSuccessResult(orderService.getOrdersPage(queryUtil));
    }


    //查看订单项
    @GetMapping("/order-items/{orderId}")
    @ResponseBody
    public Result orderItems(@PathVariable("orderId") Long orderId){
        List<NewBeeMallOrderItemVO> orderItems = orderService.getOrderItem(orderId);
        if (!CollectionUtils.isEmpty(orderItems)){
            return ResultGenerator.getSuccessResult(orderItems);
        }
        return ResultGenerator.getFailResult(ServiceResuleEnum.DATA_NOT_EXIST.getResult());
    }

    //更改订单
    @RequestMapping(value = "/orders/update",method = RequestMethod.POST)
    @ResponseBody
    public Result updateOrderInfo(@RequestBody NewBeeMallOrder newBeeMallOrder){
        if (Objects.isNull(newBeeMallOrder.getTotalPrice()) || newBeeMallOrder.getTotalPrice() < 1
            || Objects.isNull(newBeeMallOrder.getOrderId()) || newBeeMallOrder.getOrderId() <1
            || StringUtils.isEmpty(newBeeMallOrder.getUserAddress())){
            return ResultGenerator.getFailResult("参数异常！");
        }
        String result = orderService.updateOrderInfo(newBeeMallOrder);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.getSuccessResult();
        } else {
            return ResultGenerator.getFailResult(result);
        }
    }

    /**
     * 配货
     */
    @RequestMapping(value = "/orders/checkDone", method = RequestMethod.POST)
    @ResponseBody
    public Result orderCheckDone(@RequestBody Long[] ids){
        if (ids.length < 1) {
            return ResultGenerator.getFailResult("参数异常！");
        }
        String result = orderService.checkDone(ids);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.getSuccessResult();
        } else {
            return ResultGenerator.getFailResult(result);
        }
    }

    /**
     * 出库
     */
    @RequestMapping(value = "/orders/checkOut", method = RequestMethod.POST)
    @ResponseBody
    public Result orderCheckOut(@RequestBody Long[] ids){
        if (ids.length < 1) {
            return ResultGenerator.getFailResult("参数异常！");
        }
        String result = orderService.checkOut(ids);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.getSuccessResult();
        } else {
            return ResultGenerator.getFailResult(result);
        }
    }

    /*
    * 关闭订单
    * */
    @RequestMapping(value = "/orders/close", method = RequestMethod.POST)
    @ResponseBody
    public Result closeOrder(@RequestBody Long[] ids){
        if (ids.length < 1) {
            return ResultGenerator.getFailResult("参数异常！");
        }
        String result = orderService.closeOrder(ids);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.getSuccessResult();
        } else {
            return ResultGenerator.getFailResult(result);
        }
    }
}
