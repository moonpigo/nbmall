package com.nbmall.newbeemall.service.impl;

import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallGoodsSearchVO;
import com.nbmall.newbeemall.dao.NewBeeMallGoodsMapper;
import com.nbmall.newbeemall.entity.NewBeeMallGoods;
import com.nbmall.newbeemall.service.NewBeeMallGoodsService;
import com.nbmall.newbeemall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewBeeMallGoodsServiceImpl implements NewBeeMallGoodsService {

    @Autowired
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;

    @Override
    public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageQueryUtil) {
        List<NewBeeMallGoods> newBeeMallGoods = newBeeMallGoodsMapper.findNewBeeMallGoodsList(pageQueryUtil);
        int total = newBeeMallGoodsMapper.getTotalNewBeeMallGoods(pageQueryUtil);
        return new PageResult(newBeeMallGoods,total,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
    }

    @Override
    public String saveNewBeeMallGoods(NewBeeMallGoods goods) {
        if (newBeeMallGoodsMapper.insertSelective(goods) >0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }


    @Override
    public NewBeeMallGoods getGoodsBySelect(Long goodsId) {
        return newBeeMallGoodsMapper.selectByPramaryKey(goodsId);

    }


    @Override
    public String updateGoods(NewBeeMallGoods goods) {
        NewBeeMallGoods temp = newBeeMallGoodsMapper.selectByPramaryKey(goods.getGoodsId());
        if (temp == null){
            return ServiceResuleEnum.DATA_NOT_EXIST.getResult();
        }
        if (newBeeMallGoodsMapper.updateByPrimaryKeySelective(goods)>0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public List<NewBeeMallGoods> selectGoodsByBatchIds(Byte status) {
        return null;
    }

    @Override
    public boolean batchUpdateSellStatus(Long[] ids, Byte sellStatus) {
        return newBeeMallGoodsMapper.batchUpdateSellStatus(ids,sellStatus) > 0;
    }

    @Override
    public PageResult searchGoods(PageQueryUtil queryUtil) {
        /*
        * 查出所有符合要求的商品，封装进VO中，并对多余文字进行截取，封装进PageResult
        * */
        List<NewBeeMallGoods> goodsList = newBeeMallGoodsMapper.findNewBeeMallGoodsListBySearch(queryUtil);

        int total = newBeeMallGoodsMapper.getTotalNewBeeMallGoodsBySearch(queryUtil);
        List<NewBeeMallGoodsSearchVO> goodsSearchVOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)){
            goodsSearchVOs = BeanUtil.copyList(goodsList, NewBeeMallGoodsSearchVO.class);

            for (NewBeeMallGoodsSearchVO goodsSearchVO : goodsSearchVOs){
//                字符串过长导致文字超出的问题
                String goodsName = goodsSearchVO.getGoodsName();
                String goodsIntro = goodsSearchVO.getGoodsIntro();
                if (goodsName.length() > 28){
                    goodsSearchVO.setGoodsName(goodsName.substring(0,28)+"...");
                }
                if (goodsIntro.length() > 30){
                    goodsSearchVO.setGoodsIntro(goodsIntro.substring(0,30)+"...");
                }
            }
        }
        return new PageResult(goodsSearchVOs,total,queryUtil.getLimit(),queryUtil.getPage());
    }
}
