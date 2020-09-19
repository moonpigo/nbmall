package com.nbmall.newbeemall.controller.mall;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallShoppingCartItemVO;
import com.nbmall.newbeemall.controller.vo.NewBeeMallUserVO;
import com.nbmall.newbeemall.entity.NewBeeMallShoppingCartItem;
import com.nbmall.newbeemall.service.NewBeeMallShoppingCartService;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Autowired
    private NewBeeMallShoppingCartService shoppingCartService;


    /*
    * 保存条目
    * */
    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveNewBeeMallShoppingCartItem(@RequestBody NewBeeMallShoppingCartItem shoppingCartItem,
                                                 HttpSession session){
        NewBeeMallUserVO newBeeMallUserVO =(NewBeeMallUserVO)session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCartItem.setUserId(newBeeMallUserVO.getUserId());
        String saveResult = shoppingCartService.saveNewBeeMallCartItem(shoppingCartItem);
        if (ServiceResuleEnum.SUCCESS.getResult().equals(saveResult)){
            return ResultGenerator.getSuccessResult();
        }
        return ResultGenerator.getFailResult(saveResult);
    }


    @GetMapping("/shop-cart")
    public String cartListPage(HttpSession session,HttpServletRequest request){
        NewBeeMallUserVO newBeeMallUserVO = (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<NewBeeMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(newBeeMallUserVO.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)){
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(NewBeeMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (NewBeeMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems){
                priceTotal += shoppingCartItemVO.getGoodsCount()*shoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1){
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal",itemsTotal);
        request.setAttribute("priceTotal",priceTotal);
        request.setAttribute("myShoppingCartItems",myShoppingCartItems);
        return "mall/cart";
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@RequestBody NewBeeMallShoppingCartItem shoppingCartItem,HttpSession session){
        NewBeeMallUserVO user = (NewBeeMallUserVO)session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        shoppingCartItem.setUserId(user.getUserId());
        String updateResult = shoppingCartService.updateNewBeeMallCartItem(shoppingCartItem);
        //修改成功
        if (ServiceResuleEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.getSuccessResult();
        }
        //修改失败
        return ResultGenerator.getFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{shoppingCartItemId}")
    @ResponseBody
    public Result deleteShoppingCartItem(@PathVariable("shoppingCartItemId") Long shoppingCartItemId){
        if (shoppingCartService.deleteById(shoppingCartItemId)){
            return ResultGenerator.getSuccessResult();
        }
        return ResultGenerator.getFailResult(ServiceResuleEnum.OPERATE_ERROR.getResult());
    }


    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpSession session,HttpServletRequest request){
        NewBeeMallUserVO user = (NewBeeMallUserVO)session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        //根据userID得到所有条目
        List<NewBeeMallShoppingCartItemVO> myShoppingCartItems = shoppingCartService.getMyShoppingCartItems(user.getUserId());
        int priceTotal = 0;
        if (CollectionUtils.isEmpty(myShoppingCartItems)){
            //无数据则不跳转至结算页
            return "mall/cart";
        }else {
            //遍历每个条目计算总价格
            for (NewBeeMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems){
                priceTotal += shoppingCartItemVO.getGoodsCount()*shoppingCartItemVO.getSellingPrice();
            }
            if ( priceTotal<1){
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal",priceTotal);
        request.setAttribute("myShoppingCartItems",myShoppingCartItems);

        return "mall/order-settle";
    }
}
