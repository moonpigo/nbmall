package com.nbmall.newbeemall.service;


import com.nbmall.newbeemall.entity.AdminUser;

public interface AdminUserService {
    AdminUser login(String userName, String password);

    Boolean updateName(Integer loginUserId ,String loginUserName, String nickName);

    AdminUser getUserDetailById(Integer loginUserId);


    Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);
}
