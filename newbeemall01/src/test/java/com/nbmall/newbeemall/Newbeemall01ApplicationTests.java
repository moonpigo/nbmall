package com.nbmall.newbeemall;

import com.nbmall.newbeemall.entity.AdminUser;
import com.nbmall.newbeemall.entity.GoodsCategory;
import com.nbmall.newbeemall.entity.MallUser;
import com.nbmall.newbeemall.service.AdminUserService;
import com.nbmall.newbeemall.service.NewBeeMallCategoryService;
import com.nbmall.newbeemall.service.NewBeeMallUserService;
import com.nbmall.newbeemall.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@SpringBootTest
class Newbeemall01ApplicationTests {

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private NewBeeMallUserService userService;

    @Resource
    private NewBeeMallCategoryService newBeeMallCategoryService;

    @Test
    void contextLoads() {
        String MD5password = MD5Util.MD5Encode("123456","UTF-8");
        System.out.println(MD5password);
        AdminUser adminUser = adminUserService.login("admin", "123456");
        System.out.println(adminUser);
    }




}
