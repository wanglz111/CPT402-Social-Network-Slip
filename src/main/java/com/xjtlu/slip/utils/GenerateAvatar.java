package com.xjtlu.slip.utils;

import com.github.afkbrb.avatar.Avatar;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GenerateAvatar {

    public static InputStream generateOneAvatar() throws IOException {
        Avatar avatar = new Avatar();
        BufferedImage bufferedImage = avatar.generateAndGetAvatar();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
        return new ByteArrayInputStream(os.toByteArray());
    }

}
