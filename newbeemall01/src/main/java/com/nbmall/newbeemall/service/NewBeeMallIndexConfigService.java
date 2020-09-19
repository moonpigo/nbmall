package com.nbmall.newbeemall.service;

import com.nbmall.newbeemall.controller.vo.NewBeeMallIndexConfigGoodsVO;
import com.nbmall.newbeemall.entity.IndexConfig;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;

import java.util.List;

public interface NewBeeMallIndexConfigService {

    PageResult getIndexConfigPages(PageQueryUtil queryUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getConfigByid(Long id);

    boolean deleteBatch(Integer[] ids);

    List<NewBeeMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number);

}
