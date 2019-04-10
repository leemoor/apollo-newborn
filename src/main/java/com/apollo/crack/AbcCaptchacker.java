package com.apollo.crack;

import com.apollo.crack.util.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 图片验证码破解
 */
public class AbcCaptchacker extends Captchacker {
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

    /**
     * 把图片进行二值化处理
     * @param image 待处理的图片
     * @return
     */
    protected BufferedImage toBinary(BufferedImage image) {

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
    protected BufferedImage clearBorder(BufferedImage image) {
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
     * 删除噪点：八邻域
     * @param image 待处理图片
     * @return
     */
    protected BufferedImage clearNoicePoint(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        int whiteRGB = Color.WHITE.getRGB();
        int blackRGB = Color.BLACK.getRGB();
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

                // 判断八邻域是否有黑点，有则保存
                if (blackRGB == image.getRGB(i-1, j)
                        || blackRGB == image.getRGB(i-1, j-1)
                        || blackRGB == image.getRGB(i-1, j+1)
                        || blackRGB == image.getRGB(i, j-1)
                        || blackRGB == image.getRGB(i, j+1)
                        || blackRGB == image.getRGB(i+1, j)
                        || blackRGB == image.getRGB(i+1, j-1)
                        || blackRGB == image.getRGB(i+1, j+1)) {
                    binaryImage.setRGB(i, j, blackRGB);
                    continue;
                }
                binaryImage.setRGB(i, j, whiteRGB);
            }
        }
        return binaryImage;

    }

    /**
     * 删除干扰线：八邻域
     * @param image 待处理图片
     * @return
     */
    protected BufferedImage clearNoiceLine(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        int whiteRGB = Color.WHITE.getRGB();
        int blackRGB = Color.BLACK.getRGB();
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

                int sum = blackRGB == image.getRGB(i-1, j) ? 1 : 0;
                sum += blackRGB == image.getRGB(i-1, j-1) ? 1 : 0;
                sum += blackRGB == image.getRGB(i-1, j+1) ? 1 : 0;
                sum += blackRGB == image.getRGB(i, j-1) ? 1 : 0;
                sum += blackRGB == image.getRGB(i, j+1) ? 1 : 0;
                sum += blackRGB == image.getRGB(i+1, j) ? 1 : 0;
                sum += blackRGB == image.getRGB(i+1, j-1) ? 1 : 0;
                sum += blackRGB == image.getRGB(i+1, j+1) ? 1 : 0;

                // 8邻域黑点数小于4个判定为干扰线上的点
                binaryImage.setRGB(i, j, sum < 5 ? whiteRGB : blackRGB);
            }
        }
        return binaryImage;

    }

    /**
     * 流水算法"精华部分.有空隙的图彻底拆开(比如"回"字会被拆成大小俩"口"字)
     * @param image		需要拆解的图(java.awt.image.BufferedImage)
     */
    protected List<BufferedImage> splitByChar(BufferedImage image) {

        // 用于装填拆解后的图片碎块
        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
        int minPix = 50;// 单个图片最小的像素数,下面会抛弃小于这个像素数的小图块
        // 获取图片宽高
        int width = image.getWidth();
        int height = image.getHeight();
        // 用于装填每个图块的点数据
        List<HashMap<Point, Integer>> pointList = new ArrayList<HashMap<Point, Integer>>();
        // 根据宽高轮询图片中的所有点进行计算
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                //找到一个点
                label:
                if (isBlack(image.getRGB(x, y))) {
                    Point point = new Point(x, y);//java.awt.Point
                    for (HashMap<Point, Integer> pointMap : pointList) {
                        if (pointMap.get(point) != null) {
                            break label;//跳到标签处,此时不会再执行下面的内容.
                        }
                    }

                    HashMap<Point, Integer> pointMap = new HashMap<Point, Integer>();
                    // 这个用法很关键,根据Map的KEY值不能重复的特点避免重复填充point
                    pointMap.put(point, 1);
                    // 这里就是在流水啦...
                    get4Point(x, y, image, pointMap);
                    pointList.add(pointMap);
                    break;
                }
            }
        }
        // 根据提取出来的point创建各个碎图块
        for (int i = 0; i < pointList.size(); ++i) {
            HashMap<Point, Integer> pointMap = pointList.get(i);
            // 图片的左,上,右,下边界以及宽,高
            int l = 0, t = 0, r = 0, b = 0, w = 0, h = 0, index = 0;
            for (Point p : pointMap.keySet()) {
                if (index == 0) {
                    // 用第一个点来初始化碎图的四个边界
                    l = p.x;
                    t = p.y;
                    r = p.x;
                    b = p.y;
                } else {
                    // 再根据每个点与原有的点进行比较取舍四个边界的值
                    l = Math.min(l, p.x);
                    t = Math.min(t, p.y);
                    r = Math.max(r, p.x);
                    b = Math.max(b, p.y);
                }
                index++;
            }
            w = r - l + 1;
            h = b - t + 1;
            // 去除杂点(小于50像素数量的点集不要)
            if (w * h < minPix) continue;
            // 创建个图片空壳子(里面的所有点值都是0,即黑色)
            BufferedImage imgCell = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            // 先将所有点替换成白色(反正我没有找到new BufferedImage的时候可以初始化像素色值的)
            for (int x = 0; x < w; ++x) {
                for (int y = 0; y < h; ++y) {
                    imgCell.setRGB(x, y, 0xffffffff);//图片换成白底
                }
            }
            // 对应点换成黑色(如果上面换成白底的一步不做的话下面这里可以替换成白色,就变成了黑底白色的碎图了)
            for (Point p : pointMap.keySet()) {
                imgCell.setRGB(p.x - l, p.y - t, 0);
            }
            // 将切好的图放入上文传入的容器中(不传入容器的话这里可以用于返回)
            subImgs.add(imgCell);
        }
        // 耗时
        return subImgs;

    }

    /**
     * 填进来上下左右中不是白色的点
     * 递归
     * @return
     */
    private static void get4Point(int x, int y, BufferedImage img, HashMap<Point, Integer> pointMap) {
        // 左边
        Point pl = new Point(x - 1, y);
        if (x - 1 >= 0 && isBlack(img.getRGB(x - 1, y)) && pointMap.get(pl) == null) {
            pointMap.put(pl, 1);
            get4Point(x - 1, y, img, pointMap);
        }
        // 右边
        Point pr = new Point(x + 1, y);
        if (x + 1 < img.getWidth() && isBlack(img.getRGB(x + 1, y)) && pointMap.get(pr) == null) {
            pointMap.put(pr, 1);
            get4Point(x + 1, y, img, pointMap);
        }
        // 上边
        Point pt = new Point(x, y - 1);
        if (y - 1 >= 0 && isBlack(img.getRGB(x, y - 1)) && pointMap.get(pt) == null) {
            pointMap.put(pt, 1);
            get4Point(x, y - 1, img, pointMap);
        }
        // 下边
        Point pb = new Point(x, y + 1);
        if (y + 1 < img.getHeight() && isBlack(img.getRGB(x, y + 1)) && pointMap.get(pb) == null) {
            pointMap.put(pb, 1);
            get4Point(x, y + 1, img, pointMap);
        }
    }

    /**
     * 图片按指定大小归一化
     * @param images 待处理的图片列表
     * @return
     */
    protected List<BufferedImage> normalization(List<BufferedImage> images) {

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
    protected BufferedImage normalization(BufferedImage image, int width, int height) {

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
