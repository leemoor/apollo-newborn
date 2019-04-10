package com.apollo.crack.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 图片处理工具集
 */
public class ImageUtils {

    /**
     * 从指定图片文件加载图片信息
     * @param file 图片文件
     * @return
     */
    public static BufferedImage readImageFromFile(File file) throws IOException {
        return ImageIO.read(file);
    }

    /**
     * 从指定链接加载图片信息
     * @param url 图片链接
     * @return
     */
    public static BufferedImage readImageFromUrl(String url) throws IOException {
        return ImageIO.read(new URL(url));
    }

    /**
     * 把图片保存到指定路径
     * @param im 图片信息
     * @param imagePath 图片待保存路径
     * @throws IOException
     */
    public static void saveImage(RenderedImage im, String imagePath) throws IOException {
        saveImage(im, "png", imagePath);
    }

    /**
     * 把图片按指定格式，保存到指定路径
     * @param im 图片信息
     * @param formatName 图片待保存格式
     * @param imagePath 图片待保存路径
     * @throws IOException
     */
    public static void saveImage(RenderedImage im, String formatName, String imagePath) throws IOException {
        ImageIO.write(im,formatName, new File(imagePath));
    }

    /**
     * 解析R、G、B三个分量的值
     * @param argb
     * @return
     */
    public static RGBVector parseRGBVector(int argb) {
        return RGBVector.parseRGBVector(argb);
    }

    public static class RGBVector {
        private int red;
        private int green;
        private int blue;

        public static RGBVector parseRGBVector(int argb) {
            return new RGBVector(argb);
        }

        public RGBVector() {

        }

        public RGBVector(int argb) {
            red = argb >> 16 & 0xFF;
            green = argb >> 8 & 0xFF;
            blue = argb & 0xFF;
        }

        public int getRed() {
            return red;
        }

        public void setRed(int red) {
            this.red = red;
        }

        public int getGreen() {
            return green;
        }

        public void setGreen(int green) {
            this.green = green;
        }

        public int getBlue() {
            return blue;
        }

        public void setBlue(int blue) {
            this.blue = blue;
        }
    }
}
