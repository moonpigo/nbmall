package com.nbmall.newbeemall.service;


import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexCarouselVO;
import com.nbmall.newbeemall.entity.Carousel;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;

import java.util.List;

public interface NewBeeMallCarouselService {
    PageResult getCarouselsPage(PageQueryUtil queryUtil);

    String saveCarousel(Carousel carousel);

    Carousel getCarouselByPrimaryKey(Integer id);

    String updateCarousel(Carousel carousel);

    boolean deleteBatch(Integer[] ids);

    List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int num);
}
