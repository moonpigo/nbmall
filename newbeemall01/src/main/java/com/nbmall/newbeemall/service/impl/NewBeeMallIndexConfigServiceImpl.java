package com.nbmall.newbeemall.service.impl;

import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import com.nbmall.newbeemall.dao.IndexConfigMapper;
import com.nbmall.newbeemall.dao.NewBeeMallGoodsMapper;
import com.nbmall.newbeemall.entity.IndexConfig;
import com.nbmall.newbeemall.entity.NewBeeMallGoods;
import com.nbmall.newbeemall.service.NewBeeMallIndexConfigService;
import com.nbmall.newbeemall.util.BeanUtil;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewBeeMallIndexConfigServiceImpl implements NewBeeMallIndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;

    @Override
    public PageResult getIndexConfigPages(PageQueryUtil queryUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.selectByConfigType(queryUtil);
        int total = indexConfigMapper.getTotalIndexConfig(queryUtil);
        return new PageResult(indexConfigs,total,queryUtil.getLimit(),queryUtil.getPage());
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        if (indexConfigMapper.insertSelective(indexConfig) >0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null){
            ServiceResuleEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig)>0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getConfigByid(Long id) {
        return indexConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean deleteBatch(Integer[] ids) {
        return indexConfigMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<NewBeeMallIndexConfigGoodsVO> indexConfigGoodsVOS =  new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType,number);
        if (!CollectionUtils.isEmpty(indexConfigs)){

            List<Long> goodsId = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<NewBeeMallGoods> goodsList = goodsMapper.selectByPrimaryKeys(goodsId);
            indexConfigGoodsVOS = BeanUtil.copyList(goodsList,NewBeeMallIndexConfigGoodsVO.class);

            //整理商品名字和简介长度
            for (NewBeeMallIndexConfigGoodsVO indexConfigGoodsVO : indexConfigGoodsVOS){
                String goodsName = indexConfigGoodsVO.getGoodsName();
                String goodsIntro = indexConfigGoodsVO.getGoodsIntro();

                if (goodsName.length()>30){
                    String goodsNameShort = goodsName.substring(0, 30) + "...";
                    indexConfigGoodsVO.setGoodsName(goodsNameShort);
                }
                if (goodsIntro.length() > 22){
                    String goodsIntroShort = goodsIntro.substring(0, 22) + "...";
                    indexConfigGoodsVO.setGoodsIntro(goodsIntroShort);
                }
            }
        }
        return indexConfigGoodsVOS;
    }
}
