package com.nbmall.newbeemall.dao;

import com.nbmall.newbeemall.entity.AdminUser;
import org.apache.ibatis.annotations.Param;

public interface AdminUserMapper {

    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    AdminUser selectByPrimaryKey(Integer loginUserId);

    int updateByPrimaryKeySelective(AdminUser adminUser);


}
