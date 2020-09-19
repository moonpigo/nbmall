package com.nbmall.newbeemall.service.impl;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.NewBeeMallCategoryLevelEnum;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexCategoryVO;
import com.nbmall.newbeemall.controller.vo.SearchPageCategoryVO;
import com.nbmall.newbeemall.controller.vo.SecondLevelCategoryVO;
import com.nbmall.newbeemall.controller.vo.ThirdLevelCategoryVO;
import com.nbmall.newbeemall.dao.GoodsCategoryMapper;
import com.nbmall.newbeemall.entity.GoodsCategory;
import com.nbmall.newbeemall.service.NewBeeMallCategoryService;
import com.nbmall.newbeemall.util.BeanUtil;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class NewBeeMallCategoryServiceImpl implements NewBeeMallCategoryService {

    @Autowired
    GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getCategorisPage(PageQueryUtil pageQueryUtil) {
        //得到选择页的商品列表
        List<GoodsCategory> goodsCategories = goodsCategoryMapper.findGoodsCategoryList(pageQueryUtil);
        //通过count(*)得到总记录数
        int total = goodsCategoryMapper.getTotalGoodsCategories(pageQueryUtil);
        //将第一页数据，总记录数，每页条数，页数 封装到PageResult
        return new PageResult(goodsCategories, total, pageQueryUtil.getLimit(), pageQueryUtil.getPage());
    }

    @Override
    public String saveGategory(GoodsCategory goodsCategory) {
        //通过分类级别和分类名称获取，判断是否已存在该新增的分类
        GoodsCategory temp =goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(), goodsCategory.getCategoryName());
        if (temp != null){
            //存在则返回”有同级同名的分类“
            return ServiceResuleEnum.SAME_CATEGORY_EXIST.getResult();
        }
        //不存在则保存新增的分类
        if (goodsCategoryMapper.insertSelective(goodsCategory) > 0){
            //成功后返回”success“
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        //不存在且保存不成功时，”database error“
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCategory(GoodsCategory goodsCategory) {
//      数据库是否不存在该CategoryId，不存在则返回
        GoodsCategory temp1 =goodsCategoryMapper.selectByPrimaryKey(goodsCategory.getCategoryId());
        if (temp1 == null){
           return ServiceResuleEnum.DATA_NOT_EXIST.getResult();
        }
        GoodsCategory temp2 = goodsCategoryMapper.selectByLevelAndName(goodsCategory.getCategoryLevel(),goodsCategory.getCategoryName());
        if (temp2 != null && temp2.getCategoryId().equals(goodsCategory.getCategoryId())){
            //同名且同id，不能继续修改   ？？？单改排序值都不可以吗
            return ServiceResuleEnum.SAME_CATEGORY_EXIST.getResult();
        }
//        更新分类
        goodsCategory.setUpdateTime(new Date());
        if (goodsCategoryMapper.updateByPrimaryKeySelective(goodsCategory) > 0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    //查询分类ByLevelAndParentIdsAndNumber
    @Override
    public List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel) {
        return goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(parentIds, categoryLevel, 0);//0代表查询所有
    }

    @Override
    public GoodsCategory getGoodsCategoryById(Long categoryId) {
        return goodsCategoryMapper.selectByPrimaryKey(categoryId);
    }


    @Override
    public SearchPageCategoryVO getCategoriesForSearch(Long goodsCategoryId) {
        SearchPageCategoryVO searchPageCategoryVO = new SearchPageCategoryVO();
        //根据id获得当前三级分类
        GoodsCategory thirdLevelGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(goodsCategoryId);
        if (thirdLevelGoodsCategory != null && thirdLevelGoodsCategory.getCategoryLevel() == (NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel())){
            //获取当前三级分类的父分类（二级分类）
            GoodsCategory secondGoodsCategory = goodsCategoryMapper.selectByPrimaryKey(thirdLevelGoodsCategory.getParentId());
            if (secondGoodsCategory != null && secondGoodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.lEVEL_TWO.getLevel()){
                //获取当前二级分类下的三级分类List
                List<GoodsCategory> thirdGoodsCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondGoodsCategory.getCategoryId()), NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel(),Constants.SEARCH_CATEGORY_NUMBER);
                //将二级分类名字、三级分类的名字和 当前二级分类下的三级分类 封装进SearchPageCategoryVO 返回
                searchPageCategoryVO.setSecondLevelCategoryName(secondGoodsCategory.getCategoryName());
                searchPageCategoryVO.setCurrCategoryName(thirdLevelGoodsCategory.getCategoryName());
                searchPageCategoryVO.setThirdLevelCategoryList(thirdGoodsCategories);
            }
        }
        return searchPageCategoryVO;
    }

    @Override
    public List<NewBeeMallIndexCategoryVO> getCategoryForIndex() {
        List<NewBeeMallIndexCategoryVO> newBeeMallIndexCategoryVOS = new ArrayList<>();
        //得到所有一级分类
        List<GoodsCategory> firstLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.lEVEL_ONT.getLevel(), 0);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //将所有一级分类的Id作为二级分类的父Id，查找二级分类
            List<Long> firstLevelIds = firstLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            List<GoodsCategory> secondLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(firstLevelIds, NewBeeMallCategoryLevelEnum.lEVEL_TWO.getLevel(), 0);

            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //将所有二级分类的Id作为三级分类的父Id，查找三级分类
                List<Long> secondLevelIds = secondLevelCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                List<GoodsCategory> thirdLevelCategories = goodsCategoryMapper.selectByLevelAndParentIdsAndNumber(secondLevelIds, NewBeeMallCategoryLevelEnum.lEVEL_THREE.getLevel(), 0);

                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //<二级分类Id，三级分类>
                    Map<Long, List<GoodsCategory>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategory::getParentId));
                    //处理二级分类
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //遍历二级分类
                    for (GoodsCategory secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
                        //复制二级分类到 页面二级分类
                        BeanUtil.copyProperties(secondLevelCategory, secondLevelCategoryVO);
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getCategoryId())) {
                            //复制三级分类列表对象到 页面三级分类列表对象
                            List<GoodsCategory> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getCategoryId());
                            List<ThirdLevelCategoryVO> thirdLevelCategoryVOS = BeanUtil.copyList(tempGoodsCategories, ThirdLevelCategoryVO.class);

                            secondLevelCategoryVO.setThirdLevelCategoryVOS(thirdLevelCategoryVOS);
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }

                    //处理一级分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //<一级分类Id，二级分类>
                        Map<Long, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        //遍历一级分类
                        for (GoodsCategory firstLevelCategory : firstLevelCategories) {
                            NewBeeMallIndexCategoryVO indexCategoryVO = new NewBeeMallIndexCategoryVO();
                            BeanUtil.copyProperties(firstLevelCategory, indexCategoryVO);
                            if (secondLevelCategoryVOMap.containsKey(firstLevelCategory.getCategoryId())) {
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstLevelCategory.getCategoryId());
                                indexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                newBeeMallIndexCategoryVOS.add(indexCategoryVO);
                            }
                        }
                    }
                }
            }
            return newBeeMallIndexCategoryVOS;
        } else {
            return null;
        }
    }
}
