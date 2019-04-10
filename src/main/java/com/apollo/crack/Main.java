package com.apollo.crack;

import com.apollo.crack.util.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 验证码破解测试
 */
public class Main {
    public static void main(String[] args) throws IOException {
//        String capthcaDir = "/Users/flying_bird/Downloads/captcha1/";
//        String captchaUrl = "http://118.122.127.40:8989/servlet/ValidateCodeServlet?d=" + System.currentTimeMillis();
//        BufferedImage captcha = ImageUtils.readImageFromUrl(captchaUrl);
//
//        try {
//            File[] imageFiles = new File(capthcaDir + "test1/").listFiles();
//            AbcNewCaptchacker cracker = new AbcNewCaptchacker();
//            cracker.setModelPath(capthcaDir + "model1.txt");
//            //for (File imageFile : imageFiles) {
////                BufferedImage captcha = ImageUtils.readImageFromFile(imageFile);
//            	 String str =   cracker.predict(captcha) ;
//            System.out.println("result:"+str);
//                //ImageUtils.saveImage(captcha, capthcaDir + "result1/" + cracker.predict(captcha) + ".png");
//            //}
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        test1();
    }

    private static void test1() {
        String capthcaDir1 = "/Users/xxx/Downloads/captchanew/images/test1.png";

        String capthcaDir2 = "/Users/xxx/Downloads/captchanew/result/";
        String captchaUrl = "http://180.153.49.81:18989/SSO/validCodeInit?d=" + System.currentTimeMillis();
        try {
//            BufferedImage captcha = ImageUtils.readImageFromFile(new File(capthcaDir1));
            BufferedImage captcha = ImageUtils.readImageFromUrl(captchaUrl);

            ImageUtils.saveImage(captcha, capthcaDir2  + "test.png");

            AbcNewCaptchacker cracker = new AbcNewCaptchacker();
            cracker.setModelPath("/Users/xxx/Downloads/captchanew/modellmc3.txt");
            System.out.println("result2 :"+cracker.predict(captcha));

        } catch (IOException e) {
            e.printStackTrace();
        }



//        String capthcaDir = "/Users/flying_bird/Downloads/captchanew/test1/test1.jpg";
//        //String capthcaDir = "/Users/flying_bird/Downloads/captchanew/test1/test3.jpg";
//        try {
//            BufferedImage captcha = ImageUtils.readImageFromFile(new File(capthcaDir));
//            AbcNewCaptchacker cracker = new AbcNewCaptchacker();
//            cracker.setModelPath("/Users/flying_bird/Downloads/captchanew/model1.txt");
//            System.out.println("result2 :"+cracker.predict(captcha));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
