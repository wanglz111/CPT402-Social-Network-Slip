package com.xjtlu.slip.controller;

import com.xjtlu.slip.utils.CreateVerifyCodeImage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

@Controller
public class CaptchaController {
    /* 获取验证码图片*/

    @GetMapping("/getVerifyCode")
    public void getVerificationCode(HttpServletResponse response, HttpServletRequest request, HttpSession session) {
        try {
            //getVerifyCodeImg() return a ImageStream
            BufferedImage verifyImg = CreateVerifyCodeImage.getVerifyCodeImage();//
            //getVerifyCode() return a String
            char[] verifyCode = CreateVerifyCodeImage.getVerifyCode();
            //char[] to String
            String code = String.valueOf(verifyCode);
            request.getSession().setAttribute("verifyCode", code);
            response.setContentType("image/png");

            OutputStream os = response.getOutputStream(); //获取文件输出流
            ImageIO.write(verifyImg, "png", os);//输出图片流
            os.flush();
            os.close();//关闭流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
