package com.nbmall.newbeemall.service;

import com.nbmall.newbeemall.controller.vo.NewBeeMallUserVO;
import com.nbmall.newbeemall.entity.MallUser;
import com.nbmall.newbeemall.util.PageQueryUtil;
import com.nbmall.newbeemall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface NewBeeMallUserService {
    PageResult getUserPages(PageQueryUtil queryUtil);

    Boolean lockUsers(Integer[] ids,int lookStatus);

    String login(String userName, String password_md5, HttpSession session);

    String register(String loginName, String password, String mallVerifyCode);

    NewBeeMallUserVO updateUserInfo(MallUser mallUser, HttpSession session);
}
