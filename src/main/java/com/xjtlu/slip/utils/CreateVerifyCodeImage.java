package com.xjtlu.slip.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @project: ssm_sms
 * @description: Draw a captcha image
 */
public class CreateVerifyCodeImage {

    private final static int WIDTH = 90;
    private final static int HEIGHT = 35;
    private final static int FONT_SIZE = 20; //character size
    private static char[] verifyCode; //verification code


    public static BufferedImage getVerifyCodeImage() {
        //Captcha image
        BufferedImage verifyCodeImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_BGR);// create a image
        Graphics graphics = verifyCodeImage.getGraphics();

        verifyCode = generateCheckCode();
        drawBackground(graphics);
        drawRands(graphics, verifyCode);

        graphics.dispose();

        return verifyCodeImage;
    }

    public static char[] getVerifyCode() {
        return verifyCode;
    }

    private static char[] generateCheckCode() {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] rands = new char[4];
        for (int i = 0; i < 4; i++) {
            int rand = (int) (Math.random() * (10 + 26 * 2));
            rands[i] = chars.charAt(rand);
        }
        return rands;
    }

    private static void drawRands(Graphics g, char[] rands) {
        g.setFont(new Font("Console", Font.BOLD, FONT_SIZE));

        for (int i = 0; i < rands.length; i++) {

            g.setColor(getRandomColor());
            g.drawString("" + rands[i], i * FONT_SIZE + 10, 25);
        }
    }

    private static void drawBackground(Graphics g) {

        g.setColor(Color.white);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw captcha interference points
        for (int i = 0; i < 200; i++) {
            int x = (int) (Math.random() * WIDTH);
            int y = (int) (Math.random() * HEIGHT);
            g.setColor(getRandomColor());
            g.drawOval(x, y, 1, 1);

        }
    }

    private static Color getRandomColor() {
        Random ran = new Random();
        return new Color(ran.nextInt(220), ran.nextInt(220), ran.nextInt(220));
    }
}
