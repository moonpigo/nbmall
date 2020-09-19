package com.nbmall.newbeemall.common;

public enum  PayStatusEnum {

    DEFAULT(-1,"支付失败"),
    PAY_ING(0,"支付中"),
    PAY_SUCCESS(1,"支付成功");


    PayStatusEnum(int payStatus, String name) {
        this.payStatus = payStatus;
        this.name = name;
    }

    public static PayStatusEnum getPayStatusEnum(int payStatus){
        for (PayStatusEnum payStatusEnum : PayStatusEnum.values()){
            if (payStatusEnum.getPayStatus() == payStatus){
                return payStatusEnum;
            }
        }
        return DEFAULT;
    }

    private int payStatus;

    private String name;

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
