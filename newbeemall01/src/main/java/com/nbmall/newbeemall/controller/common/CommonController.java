package com.nbmall.newbeemall.controller.common;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.nbmall.newbeemall.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Properties;


@Controller
public class CommonController {

    @Autowired
    DefaultKaptcha defaultKaptcha;


    @GetMapping("/common/kaptcha")
    public void adminKaptcha(HttpServletRequest request, HttpServletResponse response)throws Exception{
        //生成验证码保存到session中
        byte[] bytes = null;
        ByteArrayOutputStream imgOutput = new ByteArrayOutputStream();
        try {
            String verifyCode = defaultKaptcha.createText();
            request.getSession().setAttribute("verifyCode",verifyCode);
            BufferedImage image = defaultKaptcha.createImage(verifyCode);
            //BufferedImage生成的图片在内存里有一个图像缓冲区
            ImageIO.write(image,"jpg",imgOutput);
        } catch (IllegalArgumentException e) {
            response.sendError(response.SC_NOT_FOUND);
            return;
        }
        //图片转字节
        bytes = imgOutput.toByteArray();
        //浏览器别缓存验证图片
        response.setHeader("Cache-controll","no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jepg");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    @GetMapping("/common/mall/kaptcha")
    public void mallKaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        DefaultKaptcha mallUserKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.textproducer.font.color", "27,174,171");
        properties.put("kaptcha.noise.color", "20,33,42");
        properties.put("kaptcha.textproducer.font.size", "30");
        properties.put("kaptcha.image.width", "110");
        properties.put("kaptcha.image.height", "40");
        properties.put("kaptcha.session.key", Constants.MALL_VERIFY_CODE_KEY);
        properties.put("kaptcha.textproducer.char.space", "2");
        properties.put("kaptcha.textproducer.char.length", "6");
        Config config = new Config(properties);
        mallUserKaptcha.setConfig(config);

        byte[] bytes = null;
        ByteArrayOutputStream imgOutput = new ByteArrayOutputStream();

        try {
            String kaptchaCode = mallUserKaptcha.createText();
            request.getSession().setAttribute(Constants.MALL_VERIFY_CODE_KEY,kaptchaCode);
            BufferedImage image = mallUserKaptcha.createImage(kaptchaCode);
            ImageIO.write(image,"jpg",imgOutput);
        } catch (IllegalArgumentException e) {
            response.sendError(response.SC_NOT_FOUND);
            return;
        }
        //图片转字节
        bytes = imgOutput.toByteArray();
        //浏览器别缓存验证图片
        response.setHeader("Cache-controller","no-store");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/jepg");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();

    }
}
