package com.nbmall.newbeemall.service.impl;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallShoppingCartItemVO;
import com.nbmall.newbeemall.dao.NewBeeMallGoodsMapper;
import com.nbmall.newbeemall.dao.NewBeeMallShoppingCartItemMapper;
import com.nbmall.newbeemall.entity.NewBeeMallGoods;
import com.nbmall.newbeemall.entity.NewBeeMallShoppingCartItem;
import com.nbmall.newbeemall.service.NewBeeMallShoppingCartService;
import com.nbmall.newbeemall.util.BeanUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PutMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NewBeeMallShoppingCartServiceImpl implements NewBeeMallShoppingCartService {

    @Resource
    private NewBeeMallShoppingCartItemMapper shoppingCartItemMapper;

    @Resource
    private NewBeeMallGoodsMapper goodsMapper;

    //todo 修改session中购物项数量

    @Override
    public String saveNewBeeMallCartItem(NewBeeMallShoppingCartItem shoppingCartItem) {
        NewBeeMallShoppingCartItem temp = shoppingCartItemMapper.selectByUserIdAndGoodsId(shoppingCartItem.getUserId(),shoppingCartItem.getGoodsId());
        if (temp != null){
            //该条目已存在则修改该条目
            temp.setGoodsCount(temp.getGoodsCount() + shoppingCartItem.getGoodsCount());
            return updateNewBeeMallCartItem(temp);
        }

        //根据goodsId查找商品
        NewBeeMallGoods newBeeMallGoods = goodsMapper.selectByPramaryKey(shoppingCartItem.getGoodsId());
        if (newBeeMallGoods == null){
            return ServiceResuleEnum.GOODS_NOT_EXIST.getResult();
        }

        //购物车条目超出限定数量（20）
        int totalItem = shoppingCartItemMapper.selectCountByUserId(shoppingCartItem.getUserId());
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER){
            return ServiceResuleEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }

        //单个商品超出限定数量（5）
        if (shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResuleEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }

        //保存条目
        if (shoppingCartItemMapper.insertSelective(shoppingCartItem) > 0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<NewBeeMallShoppingCartItemVO> shoppingCartItemVOS = new ArrayList<>();
        //根据userId查询所有的条目
        List<NewBeeMallShoppingCartItem> shoppingCartItems = shoppingCartItemMapper.selectCartItemByUserId(userId);
        if (!CollectionUtils.isEmpty(shoppingCartItems)){
            //得到所有条目中的所有的goodsId（goodsList）,根据goodsList得到所有的商品List<NewBeeMallGoods>
            List<Long> goodsIds = shoppingCartItems.stream().map(NewBeeMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<NewBeeMallGoods> goodsList = goodsMapper.selectByPrimaryKeys(goodsIds);
            //根据goodsList，封装其数据进 Map<Long,NewBeeMallGoods>
            Map<Long,NewBeeMallGoods> goodsMap = new HashMap<>();
            goodsMap = goodsList.stream().collect(Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(),(entity1, entity2) -> entity1));

            //遍历查询出的所有条目，将每个条目的goodsId、goodsCount封装进NewBeeMallShoppingCartItemVO
            for (NewBeeMallShoppingCartItem shoppingCartItem : shoppingCartItems){
                NewBeeMallShoppingCartItemVO shoppingCartItemVO = new NewBeeMallShoppingCartItemVO();
                BeanUtil.copyProperties(shoppingCartItem, shoppingCartItemVO);

                //map根据每个条目的goodsId,得到对应的goods
                // 将goods的goodsName（裁剪长度小于28）、GoodsCoverImg、SellingPrice封装进NewBeeMallShoppingCartItemVO
                if (goodsMap.containsKey(shoppingCartItem.getGoodsId())){
                    NewBeeMallGoods newBeeMallGoodsTemp = goodsMap.get(shoppingCartItem.getGoodsId());
                    String goodsName = newBeeMallGoodsTemp.getGoodsName();
                    if (goodsName.length()>28){
                        goodsName = goodsName.substring(0,28)+"...";
                    }
                    shoppingCartItemVO.setGoodsName(goodsName);
                    shoppingCartItemVO.setGoodsCoverImg(newBeeMallGoodsTemp.getGoodsCoverImg());
                    shoppingCartItemVO.setSellingPrice(newBeeMallGoodsTemp.getSellingPrice());
                    shoppingCartItemVOS.add(shoppingCartItemVO);
                }
            }
        }
        return shoppingCartItemVOS;
    }


    @Override
    public String updateNewBeeMallCartItem(NewBeeMallShoppingCartItem shoppingCartItem) {
        //根据条目id查询条目
        NewBeeMallShoppingCartItem shoppingCartItemUpdate = shoppingCartItemMapper.selectByPrimaryKey(shoppingCartItem.getCartItemId());
        if (shoppingCartItemUpdate == null){
            return ServiceResuleEnum.DATA_NOT_EXIST.getResult();
        }
        //判断单个商品数量是否大于限定
        if (shoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER){
            return ServiceResuleEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //todo 数量相同不会进行修改
        //todo userId不同不能修改
        //将数据库搜索出的条目的商品数量、时间进行更改
        shoppingCartItemUpdate.setGoodsCount(shoppingCartItem.getGoodsCount());
        shoppingCartItemUpdate.setUpdateTime(new Date());

       if (shoppingCartItemMapper.updateByPrimaryKeySelective(shoppingCartItemUpdate) > 0 ){
           //更改成功
            return ServiceResuleEnum.SUCCESS.getResult();
       };
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId) {
        //todo userId不同不能删除
       return shoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }
}

