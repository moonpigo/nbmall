package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.IndexConfig;
import com.nbmall.newbeemall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IndexConfigMapper {

    List<IndexConfig> selectByConfigType(PageQueryUtil queryUtil);

    int getTotalIndexConfig(PageQueryUtil queryUtil);

    int insertSelective(IndexConfig indexConfig);

    IndexConfig selectByPrimaryKey(Long configId);

    int updateByPrimaryKeySelective(IndexConfig indexConfig);

    int deleteBatch(@Param("ids") Integer[] ids);

    List<IndexConfig> findIndexConfigsByTypeAndNum(@Param("configType") int configType, @Param("number") int number);
}
