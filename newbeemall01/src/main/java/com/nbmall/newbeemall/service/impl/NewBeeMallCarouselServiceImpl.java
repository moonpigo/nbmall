package com.nbmall.newbeemall.service.impl;

import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexCarouselVO;
import com.nbmall.newbeemall.dao.CarouselMapper;
import com.nbmall.newbeemall.entity.Carousel;
import com.nbmall.newbeemall.service.NewBeeMallCarouselService;
import com.nbmall.newbeemall.util.BeanUtil;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NewBeeMallCarouselServiceImpl implements NewBeeMallCarouselService {

    @Resource
    private CarouselMapper carouselMapper;

    @Override
    public PageResult getCarouselsPage(PageQueryUtil queryUtil) {
        List<Carousel>  carousels =  carouselMapper.findCarouselList(queryUtil);
        int total= carouselMapper.getTotalCarousels();
        return new PageResult(carousels,total,queryUtil.getLimit(),queryUtil.getPage());
    }

    @Override
    public String saveCarousel(Carousel carousel) {
        if (carouselMapper.insertSelective(carousel) >0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselByPrimaryKey(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public String updateCarousel(Carousel carousel) {
        Carousel temp = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if (temp == null){
            return ServiceResuleEnum.DATA_NOT_EXIST.getResult();
        }
        carousel.setUpdateTime(new Date());
        if (carouselMapper.updateByPrimaryKeySelective(carousel) > 0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public boolean deleteBatch(Integer[] ids) {
        return carouselMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<NewBeeMallIndexCarouselVO> getCarouselsForIndex(int num) {
        List<NewBeeMallIndexCarouselVO> indexCarouselVOS = new ArrayList<>(num);
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(num);
        if (!CollectionUtils.isEmpty(carousels)){
            indexCarouselVOS = BeanUtil.copyList(carousels,NewBeeMallIndexCarouselVO.class);
        }
        return indexCarouselVOS;
    }


}
