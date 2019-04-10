package com.apollo.crack;

import com.apollo.crack.util.CrackUtils;
import com.apollo.crack.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 图片验证码破解
 */
public class AbcNewCaptchacker extends Captchacker {
    /**
     * 获取图片特征
     */
    protected String getCharacteristicStr(BufferedImage image) {
        StringBuffer sb = new StringBuffer();
        int width = image.getWidth();
        int height = image.getHeight();
        int whiteRGB = Color.WHITE.getRGB();
        sb.append(0);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                sb.append(" ").append(i * height + j + 1).append(":").append(whiteRGB == image.getRGB(i, j) ? 0 : 1);
            }
        }
        return sb.toString();
    }

    protected List<BufferedImage> captchaSplit(BufferedImage image) {
        // 验证码字符切割：垂直边界切割
		List<BufferedImage> bilist = null;
//		try {
			// 通过颜色粗处理干扰线
//			System.out.println("11111");
			BufferedImage noColorNoiceLingImage = CrackUtils.clearNoiceLineByColor(image);
//			ImageUtils.saveImage(noColorNoiceLingImage,  "g:\\captcha\\result1\\" +System.currentTimeMillis() +".png");
			
			// 图片二值化
			BufferedImage binaryImage = CrackUtils.toBinary(noColorNoiceLingImage);
//			ImageUtils.saveImage(noColorNoiceLingImage,  "g:\\captcha\\result1\\" +System.currentTimeMillis() +".png");

			// 删除边线
			BufferedImage noBorderImage = CrackUtils.clearBorder(binaryImage);
//			ImageUtils.saveImage(noColorNoiceLingImage,  "g:\\captcha\\result1\\" +System.currentTimeMillis() +".png");

			bilist = CrackUtils.splitByCharByMinPixNum(noBorderImage);

            bilist = CrackUtils.normalization(bilist);
            for (BufferedImage bi : bilist) {
//                ImageUtils.saveImage(bi,  "/Users/cdlimingcheng/Downloads/captchanew/result/" +System.currentTimeMillis() +".png");
            }

//		} catch (IOException e) {
//			System.err.println("save error");
//		}
        return bilist;
    }

    protected BufferedImage toBinary(BufferedImage image) {
        return null;
    }

    protected BufferedImage clearBorder(BufferedImage image) {
        return null;
    }

    protected BufferedImage clearNoicePoint(BufferedImage image) {
        return null;
    }

    protected BufferedImage clearNoiceLine(BufferedImage image) {
        return null;
    }

    protected List<BufferedImage> splitByChar(BufferedImage image) {
        return null;
    }

    protected List<BufferedImage> normalization(List<BufferedImage> images) {
        return null;
    }

    protected BufferedImage normalization(BufferedImage image, int width, int height) {
        return null;
    }
}
