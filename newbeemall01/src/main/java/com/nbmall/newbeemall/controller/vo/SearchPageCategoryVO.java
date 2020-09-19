package com.nbmall.newbeemall.controller.vo;

import com.nbmall.newbeemall.entity.GoodsCategory;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索页面分类数据VO
 */
public class SearchPageCategoryVO implements Serializable {

    private String firstLevelCategoryName;

    private List<GoodsCategory> SecondLevelCategoryList;

    private String secondLevelCategoryName;

    private List<GoodsCategory> thirdLevelCategoryList;

    private String currCategoryName;

    public String getFirstLevelCategoryName() {
        return firstLevelCategoryName;
    }

    public void setFirstLevelCategoryName(String firstLevelCategoryName) {
        this.firstLevelCategoryName = firstLevelCategoryName;
    }

    public List<GoodsCategory> getSecondLevelCategoryList() {
        return SecondLevelCategoryList;
    }

    public void setSecondLevelCategoryList(List<GoodsCategory> secondLevelCategoryList) {
        SecondLevelCategoryList = secondLevelCategoryList;
    }

    public String getSecondLevelCategoryName() {
        return secondLevelCategoryName;
    }

    public void setSecondLevelCategoryName(String secondLevelCategoryName) {
        this.secondLevelCategoryName = secondLevelCategoryName;
    }

    public List<GoodsCategory> getThirdLevelCategoryList() {
        return thirdLevelCategoryList;
    }

    public void setThirdLevelCategoryList(List<GoodsCategory> thirdLevelCategoryList) {
        this.thirdLevelCategoryList = thirdLevelCategoryList;
    }

    public String getCurrCategoryName() {
        return currCategoryName;
    }

    public void setCurrCategoryName(String currCategoryName) {
        this.currCategoryName = currCategoryName;
    }
}
