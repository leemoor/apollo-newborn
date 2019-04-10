package com.apollo.crack;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_predict_ex;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证码破解
 */
public abstract class Captchacker {
    private String modelPath;
    private static svm_model model;
    private static Object lock = new Object();

    private svm_model getModel() throws IOException {
        if (model == null) {
            synchronized (lock) {
                if (model == null) {
                    model = svm.svm_load_model(modelPath);
                }
            }
        }
        return model;
    }

    public String predict(BufferedImage image) throws IOException {
        return svm_predict_ex.predict(getCharacteristicStrs(captchaSplit(image)), getModel());
    }

    /**
     * 获取图片特征
     */
    protected List<String> getCharacteristicStrs(List<BufferedImage> images) {

        List<String> lines = new ArrayList<String>();
        for (BufferedImage image : images) {
            lines.add(getCharacteristicStr(image));
        }
        return lines;

    }

    /**
     * 获取图片特征
     */
    protected abstract String getCharacteristicStr(BufferedImage image);
//    protected  abstract List<BufferedImage> captchaSplit(BufferedImage image);

    protected  List<BufferedImage> captchaSplit(BufferedImage image) {
        // 图片二值化
        BufferedImage binaryImage = toBinary(image);

        // 删除边线
        BufferedImage noBorderImage = clearBorder(binaryImage);

        // 去噪点
        BufferedImage noNoicePointImage = clearNoicePoint(noBorderImage);

        // 去干扰线
        BufferedImage noNoiceLingImage = clearNoiceLine(noNoicePointImage);

        // 验证码字符切割
        List<BufferedImage> charImages = splitByChar(noNoiceLingImage);//流水算法

        // 大小归一化
        return normalization(charImages);
    }

    /**
     * 把图片进行二值化处理
     * @param image 待处理的图片
     * @return
     */
    protected abstract BufferedImage toBinary(BufferedImage image);

    /**
     * 删除图片边线
     * @param image 待处理图片
     * @return
     */
    protected abstract BufferedImage clearBorder(BufferedImage image);

    /**
     * 删除噪点
     * @param image 待处理图片
     * @return
     */
    protected abstract BufferedImage clearNoicePoint(BufferedImage image);

    /**
     * 删除干扰线
     * @param image 待处理图片
     * @return
     */
    protected abstract BufferedImage clearNoiceLine(BufferedImage image);

    /**
     * 字符拆分
     * @param image 需要拆解的图(java.awt.image.BufferedImage)
     */
    protected abstract List<BufferedImage> splitByChar(BufferedImage image);

    /**
     * 图片按指定大小归一化
     * @param images 待处理的图片列表
     * @return
     */
    protected abstract List<BufferedImage> normalization(List<BufferedImage> images);

    /**
     * 图片按指定大小归一化
     * @param image 待处理图片
     * @param width 归一化设置宽度
     * @param height 归一化设置高度
     * @return
     */
    protected abstract BufferedImage normalization(BufferedImage image, int width, int height);

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }
}
