package com.nbmall.newbeemall.controller.common;

import com.nbmall.newbeemall.common.Constants;
import com.nbmall.newbeemall.util.NewBeeMallUtils;
import com.nbmall.newbeemall.util.Result;
import com.nbmall.newbeemall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Controller
public class UploadController {

    @ResponseBody
    @PostMapping("/admin/upload/file")
    public Result uploadFile(@RequestParam MultipartFile file, HttpServletRequest request) throws URISyntaxException {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random random = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(format.format(new Date())).append(random.nextInt(100)).append(suffix);
        String newFileName = tempName.toString();
        File fileDirectory = new File(Constants.FILE_UPLOAD_DIC);
        File destFile = new File(Constants.FILE_UPLOAD_DIC + newFileName);

        try {
            if (!fileDirectory.exists()){
                if (!fileDirectory.mkdirs()){
                    throw new IOException("创建文件路径失败:"+fileDirectory);
                }
            }
            file.transferTo(destFile);
            Result resultSeccuss = ResultGenerator.getSuccessResult();
            //System.out.println(request.getRequestURL());       http://localhost:28088/admin/upload/file
            resultSeccuss.setData(NewBeeMallUtils.getHost(new URI(request.getRequestURL()+"")) +"/upload/"+newFileName);
           //http://localhost:28088/upload/20200824_10274961.jpg
            return resultSeccuss;
        } catch (IOException e) {
            e.printStackTrace();
            return ResultGenerator.getFailResult("文件上传失败");
        }
    }
}
