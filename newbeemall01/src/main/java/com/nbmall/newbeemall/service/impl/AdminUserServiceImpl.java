package com.nbmall.newbeemall.service.impl;

import com.nbmall.newbeemall.dao.AdminUserMapper;
import com.nbmall.newbeemall.entity.AdminUser;
import com.nbmall.newbeemall.service.AdminUserService;
import com.nbmall.newbeemall.util.MD5Util;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminUserServiceImpl implements AdminUserService {


    @Resource
    private AdminUserMapper adminUserMapper;

//    登录方法
    @Override
    public AdminUser login(String userName, String password) {
        String MD5password = MD5Util.MD5Encode(password, "UTF-8");
        return adminUserMapper.login(userName,MD5password);
    }


//    根据id查询admin
    @Override
    public AdminUser getUserDetailById(Integer loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }


//    更改名称
    @Override
    public Boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
//        根据id查询出admin
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //，如果不为空则设置新名称
        if (null != adminUser){
            adminUser.setAdminUserName(loginUserName);
            adminUser.setNickName(nickName);
            //更新名称
            if (adminUserMapper.updateByPrimaryKeySelective(adminUser)>0){
                //修改成功返回true
                return true;
            }
        }
        //修改成功返回false
        return false;
    }


    //更改密码
    @Override
    public Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null){
            String MD5OriginalPassword = MD5Util.MD5Encode(originalPassword,"UTF-8");
            String MD5newPassword = MD5Util.MD5Encode(newPassword,"UTF-8");
            //判断原密码是否正确
            if ( MD5OriginalPassword.equals(adminUser.getAdminPassword())){
                //正确则修改为新密码
                adminUser.setAdminPassword(MD5newPassword);
                if (adminUserMapper.updateByPrimaryKeySelective(adminUser) >0){
                    return true;
                }
            }
        }
        return false;
    }
}
