package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.Carousel;
import com.nbmall.newbeemall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselMapper {

    List<Carousel> findCarouselList(PageQueryUtil queryUtil);

    int getTotalCarousels();

    int insertSelective(Carousel carousel);

    Carousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(Carousel carousel);

    int deleteBatch(@Param("ids") Integer[] ids);

    List<Carousel> findCarouselsByNum(int num);
}
