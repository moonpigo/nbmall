package com.nbmall.newbeemall.util;

public class NumberUtil {

    /*
    生成随机订单号
    * */
    public static String getOrderNo(){
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(System.currentTimeMillis()));
        stringBuilder.append(getRandomNum(4));
        return stringBuilder.toString();
    }

    /*
    * 生成指定长度的随机数??
    * */
    public static int getRandomNum(int length){
        int num = 1;
        double random = Math.random();
        if (random < 0.1){
            random = random + 0.1;
        }
        for (int i =0; i<length; i++){
            num = num * 10;
        }
        return (int)random * num;
    }
}
