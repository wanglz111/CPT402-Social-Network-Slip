package com.xjtlu.slip.controller;

import com.xjtlu.slip.utils.CreateVerifyCodeImage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class CaptchaController {
    /* Get the verification code picture*/

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

            OutputStream os = response.getOutputStream(); //Get file output stream
            ImageIO.write(verifyImg, "png", os);//output image stream
            os.flush();
            os.close();//close stream
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
