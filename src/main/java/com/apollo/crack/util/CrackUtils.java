package com.apollo.crack.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 破解相关工具集
 */
public class CrackUtils {
    /**
     * 图片按指定大小归一化
     * @param images 待处理的图片列表
     * @return
     */
    public static List<BufferedImage> normalization(List<BufferedImage> images) {

        List<BufferedImage> result = new ArrayList<BufferedImage>();
        for (BufferedImage image : images) {
            result.add(normalization(image, 26, 27));
        }

        return result;

    }

    /**
     * 图片按指定大小归一化
     * @param image 待处理图片
     * @param width 归一化设置宽度
     * @param height 归一化设置高度
     * @return
     */
    public static BufferedImage normalization(BufferedImage image, int width, int height) {

        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        int whiteRGB = Color.WHITE.getRGB();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i < srcWidth && j < srcHeight) {
                    binaryImage.setRGB(i, j, image.getRGB(i, j));
                    continue;
                }
                binaryImage.setRGB(i, j, whiteRGB);
            }
        }
        return binaryImage;

    }

    /**
     * 删除干扰线：通过颜色过滤
     * @param image 待处理图片
     * @return
     */
    public static BufferedImage clearNoiceLineByColor(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int whiteRGB = Color.WHITE.getRGB();
        for (int i = 0 ; i < width; i++) {
            for (int j = 0 ; j < height; j++) {
                int rgb = image.getRGB(i, j);
                if (i == 0 || j == 0 || i == width - 1 || j == height - 1) {
                    binaryImage.setRGB(i, j, rgb);
                    continue;
                }

                if (rgb == whiteRGB) {
                    binaryImage.setRGB(i, j, whiteRGB);
                    continue;
                }

                ImageUtils.RGBVector rgbVector = ImageUtils.parseRGBVector(rgb);
                if ((rgbVector.getRed() + rgbVector.getGreen() + rgbVector.getBlue()) / 3 < 20) {
                    binaryImage.setRGB(i, j, whiteRGB);
                    continue;
                }
                binaryImage.setRGB(i, j, rgb);
            }
        }
        return binaryImage;
    }

    /**
     * 把图片进行二值化处理
     * @param image 待处理的图片
     * @return
     */
    public static BufferedImage toBinary(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        int whiteRGB = Color.WHITE.getRGB();
        int blackRGB = Color.BLACK.getRGB();
        for (int i = 0 ; i < width ; i++) {
            for (int j = 0 ; j < height; j++) {
                ImageUtils.RGBVector rgb = ImageUtils.parseRGBVector(image.getRGB(i, j));
                int min = Math.min(rgb.getRed(), rgb.getGreen());
                min = Math.min(min, rgb.getBlue());
                if (min > 200) {
                    binaryImage.setRGB(i, j, whiteRGB);
                } else {
                    binaryImage.setRGB(i, j, blackRGB);
                }
            }
        }
        return binaryImage;
    }

    /**
     * 删除图片边线
     * @param image 待处理图片
     * @return
     */
    public static BufferedImage clearBorder(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        int whiteRGB = Color.WHITE.getRGB();
        for (int i = 0 ; i < width ; i++) {
            for (int j = 0 ; j < height; j++) {
                // 边线点设为白色点
                if (i == 0 || j == 0 || i == width - 1 || j == height - 1) {
                    binaryImage.setRGB(i, j, whiteRGB);
                    continue;
                }
                binaryImage.setRGB(i, j, image.getRGB(i, j));
            }
        }

        return binaryImage;
    }

    /**
     * 根据图片垂直像素最小的3个点切割
     * @param image		需要拆解的图(java.awt.image.BufferedImage)
     */
    public static List<BufferedImage> splitByCharByMinPixNum(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixNums = new int[width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixNums[i] += isBlack(image.getRGB(i, j)) ? 1 : 0;
            }
        }

        int s = 0;
        int w = 0;
        List<Integer> starts = new ArrayList<Integer>();
        List<Integer> widths = new ArrayList<Integer>();
        for (int i = 0; i < width; i++) {
            if (pixNums[i] > 0) {
                w++;
            } else {
                if (w > 1) {
                    starts.add(s);
                    widths.add(w);
                }
                w = 0;
                s = i;
            }
        }

        // 切割后的图片
        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
        for (int p = 0; p < starts.size(); p++) {
            int tStart = starts.get(p);
            int tWidth = widths.get(p);
            BufferedImage binaryImage = new BufferedImage(tWidth, height, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < tWidth; i++) {
                for (int j = 0; j < height; j++) {
                    binaryImage.setRGB(i, j, image.getRGB(i+tStart, j));
                }
            }
            subImgs.add(binaryImage);
        }

        return subImgs;
    }

    /**
     * 判断是不是黑色[这里的黑色指暗色],实际上本程序处理过的颜色,黑就是纯黑,值=0
     * @param colorInt
     * @return
     */
    public static boolean isBlack(int colorInt) {
        int threshold = 150;// 色域,用于界定多少范围的色值是噪色
        Color color = new Color(colorInt);
        return color.getRed() + color.getGreen() + color.getBlue() <= threshold * 3;
    }
}
