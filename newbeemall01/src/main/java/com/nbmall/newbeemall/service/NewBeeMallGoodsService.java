package com.nbmall.newbeemall.service;

import com.nbmall.newbeemall.entity.NewBeeMallGoods;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;
import com.nbmall.newbeemall.util.Result;

import java.util.List;

public interface NewBeeMallGoodsService {
    PageResult getNewBeeMallGoodsPage(PageQueryUtil pageQueryUtil);

    String saveNewBeeMallGoods(NewBeeMallGoods goods);

    NewBeeMallGoods getGoodsBySelect(Long goodsId);

    String updateGoods(NewBeeMallGoods goods);

    List<NewBeeMallGoods> selectGoodsByBatchIds(Byte status);

    boolean batchUpdateSellStatus(Long[] ids, Byte sellStatus);

    PageResult searchGoods(PageQueryUtil queryUtil);
}
