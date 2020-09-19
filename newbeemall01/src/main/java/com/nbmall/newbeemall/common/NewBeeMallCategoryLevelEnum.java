package com.nbmall.newbeemall.common;

public enum NewBeeMallCategoryLevelEnum {

    DEFAULT(0,"ERROR"),
    lEVEL_ONT(1,"一级分类"),
    lEVEL_TWO(2,"二级分类"),
    lEVEL_THREE(3,"三级分类");

    private int level;
    private String name;


    NewBeeMallCategoryLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static NewBeeMallCategoryLevelEnum getNewBeeMallOrderStatusEnumByLevel(int level){
        for (NewBeeMallCategoryLevelEnum newBeeMallCategoryLevelEnum : NewBeeMallCategoryLevelEnum.values()){
            if (newBeeMallCategoryLevelEnum.getLevel() == level){
                return newBeeMallCategoryLevelEnum;
            }
        }
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
