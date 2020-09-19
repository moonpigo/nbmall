package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.MallUser;
import com.nbmall.newbeemall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewBeeMallUserMapper {

    List<MallUser> selectUser(PageQueryUtil queryUtil);

    int getTotalMallUser();

    int lockUserBatch(@Param("ids") Integer[] ids,@Param("lookStatus") int lookStatus);

    MallUser selectByNameAndPass(@Param("userName") String userName, @Param("password_md5")String password_md5);

    MallUser selectByLoginName(String loginName);

    int insertSelective(MallUser mallUser);

//    int updateInfo();

    MallUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(MallUser userFromDB);
}
