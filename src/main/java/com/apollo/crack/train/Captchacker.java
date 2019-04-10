package com.apollo.crack.train;

import com.apollo.crack.util.CrackUtils;
import com.apollo.crack.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import libsvm.svm_predict;
import libsvm.svm_train;

/**
 * 图片验证码破解
 */
public class Captchacker {
    public static void main(String[] args) throws IOException {
//        for (int i = 0; i < 100; i++) {
//            genCharImages();
//            System.out.println("生成图片次数：" + i);
//        }
    	//生成训练数据
        genTrainData();
    	//训练模型
        train();
    }

    /**
     * 生成训练样本
     * @throws FileNotFoundException
     */
    public static void genTrainData() throws FileNotFoundException {
        String capthcaDir = "/Users/flying_bird/Downloads/captcha1/";
        String trainFilePath = capthcaDir + "trainlmc2.txt";
        PrintWriter pw = new PrintWriter(new File(trainFilePath));
        try {
            for (int i = 0; i < 10; i++) {
                File subDir = new File(capthcaDir + i + "/");
                File[] imageFiles = subDir.listFiles();
                for (File imageFile : imageFiles) {
                    BufferedImage image = ImageUtils.readImageFromFile(imageFile);
                    if(image == null){
                        System.out.println(imageFile.getAbsolutePath()+" --------");
                        continue;
                    }
                    String line = toGetTrainData(i, ImageUtils.readImageFromFile(imageFile));
                    pw.write(line + "\n");
                    System.out.print(line + "\n");
                }
            }
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pw.close();
        }
    }

    public static String toGetTrainData(int categoryId, BufferedImage image) {

        StringBuffer sb = new StringBuffer();
        if(image == null){
            System.out.println(categoryId+" is null");
        }
        int width = image.getWidth();
        int height = image.getHeight();
        int whiteRGB = Color.WHITE.getRGB();
        sb.append(categoryId);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sb.append(" ").append(i * height + j + 1).append(":").append(whiteRGB == image.getRGB(i, j) ? 0 : 1);
            }
        }
        return sb.toString();
    }

    /**
     * 生成单个字符的图片
     * @throws IOException
     */
    public static void genCharImages() throws IOException  {
        String capthcaDir = "/Users/flying_bird/Downloads/captcha2/";
        String captchaUrl = "http://118.122.127.40:8989/servlet/ValidateCodeServlet?d=" + System.currentTimeMillis();
        BufferedImage captcha = ImageUtils.readImageFromUrl(captchaUrl);

//        ImageUtils.saveImage(captcha, capthcaDir + System.currentTimeMillis() + ".png");

        // 通过颜色粗处理干扰线
        BufferedImage noColorNoiceLingImage = CrackUtils.clearNoiceLineByColor(captcha);
//        ImageUtils.saveImage(noColorNoiceLingImage, capthcaDir + System.currentTimeMillis() + "-ccl.png");

        // 图片二值化
        BufferedImage binaryImage = CrackUtils.toBinary(noColorNoiceLingImage);
//        ImageUtils.saveImage(binaryImage, capthcaDir + System.currentTimeMillis() + "-b.png");

        // 删除边线
        BufferedImage noBorderImage = CrackUtils.clearBorder(binaryImage);
//        ImageUtils.saveImage(noBorderImage, capthcaDir + System.currentTimeMillis() + "-nb.png");

        // 验证码字符切割
        List<BufferedImage> subImgs = CrackUtils.splitByCharByMinPixNum(noBorderImage); // 垂直边界切割
        for (int i = 0; i < subImgs.size(); i++) {
            BufferedImage bi = subImgs.get(i);
//            ImageUtils.saveImage(bi, capthcaDir + System.currentTimeMillis() + "-sub" + i + ".png");
        }

        // 大小归一化
        subImgs = CrackUtils.normalization(subImgs);
        for (int i = 0; i < subImgs.size(); i++) {
            BufferedImage bi = subImgs.get(i);
            ImageUtils.saveImage(bi, capthcaDir + System.currentTimeMillis() + "-sub" + i + "-nm.png");
        }
    }
    
	public static void train() {

	    String []arg ={ "/Users/xxx/Downloads/captcha1/train1.txt", //存放SVM训练模型用的数据的路径
	    		"/Users/xxx/Downloads/captcha1/model1.txt"};  //存放SVM通过训练数据训/ //练出来的模型的路径

	    String []parg={"trainfile/train2.txt",   //这个是存放测试数据
	    		       "trainfile/model_r.txt",  //调用的是训练以后的模型
	    		        "trainfile/out_r.txt"};  //生成的结果的文件的路径
	    System.out.println("........SVM运行开始..........");  
	    //创建一个训练对象
	    svm_train t = new svm_train();
        svm_predict p= new svm_predict();
	    try {
			t.main(arg);
            //p.main(parg);
		} catch (IOException e) {
		}   //调用
	}
}


