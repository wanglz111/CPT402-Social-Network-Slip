package com.xjtlu.slip;

import com.xjtlu.slip.utils.CreateVerifyCodeImage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;

public class TestCaptcha {

    @Test
    public void testCaptcha() {
        CreateVerifyCodeImage.getVerifyCodeImage();
        System.out.println(CreateVerifyCodeImage.getVerifyCode());
//        ImageIO.write(CreateVerifyCodeImage.getVerifyCodeImage(), "1.png", "./static/images/1.png");//输出图片流
    }
}
