package com.nbmall.newbeemall.common;

public enum ServiceResuleEnum {

    SUCCESS("success"),

    SAME_CATEGORY_EXIST("有同级同名的分类"),

    DB_ERROR("database error"),

    DATA_NOT_EXIST("数据库不存在"),

    LOGIN_NAME_NULL("请输入登录名！"),

    LOGIN_PASSWORD_NULL("请输入密码！"),

    LOGIN_VERIFY_CODE_NULL("请输入验证码！"),

    LOGIN_VERIFY_CODE_ERROR("验证码错误！"),

    LOGIN_USER_LOCKED("用户已被禁止登录！"),

    LOGIN_ERROR("登录失败！"),

    USERNAME_OR_PASSWORD_ERROR("用户名或密码错误"),

    SAVE_LOGIN_NAME_EXIST("该用户已存在"),

    GOODS_NOT_EXIST("商品不存在！"),

    GOODS_PUT_DOWN("商品已下架！"),

    SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR("超出单个商品的最大购买数量！"),

    SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR("超出购物车最大容量！"),

    OPERATE_ERROR("操作失败！"),

    NULL_ADDRESS_ERROR("地址不能为空！"),

    GOODS_PUT_DOWN_NO_SELLING("已下架,不能生成订单！"),

    SHOPPING_ITEM_COUNT_ERROR("库存不足！"),

    ORDER_PRICE_ERROR("订单价格异常！"),

    ORDER_ERROR("订单异常！"),

    ORDER_NOT_EXIST_ERROR("订单不存在！"),

    ORDER_NOT_PRE_PAY("订单非待支付状态"),

    SHOPPING_ITEM_ERROR("购物车数据异常！");

    private String result;

    ServiceResuleEnum(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
