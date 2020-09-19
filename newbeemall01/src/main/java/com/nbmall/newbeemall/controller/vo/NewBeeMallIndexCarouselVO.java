package com.nbmall.newbeemall.controller.vo;

import java.io.Serializable;


/**
 * 首页轮播图VO
 */
public class NewBeeMallIndexCarouselVO implements Serializable {
    private String carouselUrl; //轮播图

    private String redirectUrl;  //点击后的跳转地址(默认不跳转)

    public String getCarouselUrl() {
        return carouselUrl;
    }

    public void setCarouselUrl(String carouselUrl) {
        this.carouselUrl = carouselUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
