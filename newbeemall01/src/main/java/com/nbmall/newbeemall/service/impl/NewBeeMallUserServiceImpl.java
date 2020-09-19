package com.nbmall.newbeemall.service.impl;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.common.ServiceResuleEnum;
import com.nbmall.newbeemall.controller.vo.NewBeeMallUserVO;
import com.nbmall.newbeemall.dao.NewBeeMallShoppingCartItemMapper;
import com.nbmall.newbeemall.dao.NewBeeMallUserMapper;
import com.nbmall.newbeemall.entity.MallUser;
import com.nbmall.newbeemall.service.NewBeeMallUserService;
import com.nbmall.newbeemall.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class NewBeeMallUserServiceImpl implements NewBeeMallUserService {
    @Autowired
    private NewBeeMallUserMapper userMapper;
    @Autowired
    private NewBeeMallShoppingCartItemMapper shoppingCartItemMapper;

    @Override
    public PageResult getUserPages(PageQueryUtil queryUtil) {
        List<MallUser> mallUsers = userMapper.selectUser(queryUtil);
        int total = userMapper.getTotalMallUser();
        return new PageResult(mallUsers,total,queryUtil.getLimit(),queryUtil.getPage());
    }

    @Override
    public Boolean lockUsers(Integer[] ids, int lookStatus) {
        if (ids.length < 1){
            return false;
        }
        return userMapper.lockUserBatch(ids,lookStatus)>0 ;
    }

    @Override
    public String login(String userName, String password, HttpSession session) {
        String password_md5 = MD5Util.MD5Encode(password,"utf-8");
        MallUser user = userMapper.selectByNameAndPass(userName,password_md5);
        if (user != null && session != null){
            if (user.getLockedFlag() == 1){ //用户被锁定，不能登陆
                return ServiceResuleEnum.LOGIN_USER_LOCKED.getResult();
            }

            //昵称太长 影响页面展示
            if (user.getNickName()!=null && user.getNickName().length()>7){
                String tempNickName = user.getNickName().substring(0,7)+"...";
                user.setNickName(tempNickName);
            }

            NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
            BeanUtils.copyProperties(user,newBeeMallUserVO);

            int shopCartItemCount = shoppingCartItemMapper.selectCountByUserId(user.getUserId());
            newBeeMallUserVO.setShopCartItemCount(shopCartItemCount);

            session.setAttribute(Constants.MALL_USER_SESSION_KEY,newBeeMallUserVO);
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.USERNAME_OR_PASSWORD_ERROR.getResult();
    }

    @Override
    public String register(String loginName, String password, String mallVerifyCode) {
        MallUser user = userMapper.selectByLoginName(loginName);
        if (user != null){
            //用户已存在
            return ServiceResuleEnum.SAVE_LOGIN_NAME_EXIST.getResult();
        }
        String password_md5 = MD5Util.MD5Encode(password,"utf-8");
        MallUser mallUser = new MallUser();
        mallUser.setLoginName(loginName);
        mallUser.setNickName(loginName);
        mallUser.setPasswordMd5(password_md5);
        if (userMapper.insertSelective(mallUser)>0){
            return ServiceResuleEnum.SUCCESS.getResult();
        }
        return ServiceResuleEnum.DB_ERROR.getResult();
    }

    @Override
    public NewBeeMallUserVO updateUserInfo(MallUser mallUser,HttpSession session) {
        NewBeeMallUserVO userTemp =  (NewBeeMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallUser userFromDB = userMapper.selectByPrimaryKey(userTemp.getUserId());
        if (userFromDB != null){
            userFromDB.setNickName(NewBeeMallUtils.cleanString(mallUser.getNickName()));
            userFromDB.setIntroduceSign(NewBeeMallUtils.cleanString(mallUser.getIntroduceSign()));
            userFromDB.setAddress(NewBeeMallUtils.cleanString(mallUser.getAddress()));
            if (userMapper.updateByPrimaryKeySelective(userFromDB) > 0){
                NewBeeMallUserVO newBeeMallUserVO = new NewBeeMallUserVO();
                userFromDB = userMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtils.copyProperties(userFromDB,newBeeMallUserVO);
                session.setAttribute(Constants.MALL_USER_SESSION_KEY,newBeeMallUserVO);
                return newBeeMallUserVO;
            }
        }
        return null;
    }
}
