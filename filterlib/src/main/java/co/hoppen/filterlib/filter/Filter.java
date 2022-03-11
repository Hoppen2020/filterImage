package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;

/**
 * Created by YangJianHui on 2021/9/10.
 */
public abstract class Filter {
    private Bitmap originalImage;
    private float resistance;

    Filter(){
    }

    public Filter setOriginalImage(Bitmap originalImage) {
        this.originalImage = originalImage;
        return this;
    }

    public Filter setResistance(float resistance) {
        this.resistance = resistance;
        return this;
    }

    protected Bitmap getOriginalImage() {
        return originalImage;
    }

    protected float getResistance() {
        return resistance;
    }

    protected boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    public abstract FilterInfoResult onFilter();


    public double rgb2Hsv(int rgb) {
        int r = Color.red(rgb);
        int g = Color.green(rgb);
        int b = Color.blue(rgb);
        double h = 0;
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        if (max == min) {
            h = 0;
        } else {
            if (r == max)
                h = (double)(g - b) / (max - min);
            else if (g == max)
                h = 2 + (double)(b - r) / (max - min);
            else
                h = 4 + (double)(r - g) / (max - min);
        }
        h = h * 60;
        if (h < 0)
            h = h + 360;
        return h;
    }

    public Bitmap hairRemoval(){

        return null;
    }

    protected Bitmap toGray(boolean dark){
        if (originalImage==null)return null;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int count = width * height;
        int [] originalPixels = new int[count];
        int [] filterPixels = new int[count];
        originalImage.getPixels(originalPixels,0,width,0,0,width,height);

        for (int i = 0; i <originalPixels.length ; i++) {
            int originalPixel = originalPixels[i];
            int R = Color.red(originalPixel);
            int G = Color.green(originalPixel);
            int B = Color.blue(originalPixel);
            int gray = (int) (R * 0.3 + G * 0.59 + B * 0.11);
            int rgb = Color.rgb(gray, gray, gray);
            int color = (int) (rgb + (rgb-128) * (1.0f+0f) /255);
            filterPixels[i] = dark?color:rgb;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        bitmap.setPixels(filterPixels,0,width, 0, 0, width, height);
        return bitmap;
    }

    public int oppositeColorValue(int value){
        int opposite = 255 - value;
        if(opposite>64 && opposite<128)
            opposite-=64;
        else if(opposite>=128 && opposite<192)
            opposite+=64;
        return opposite;
    }

    public Bitmap getFaceSkin(){
        if (originalImage!=null){
            Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat yuvMat = new Mat();
            Utils.bitmapToMat(bitmap,yuvMat);
            Imgproc.cvtColor(yuvMat,yuvMat,Imgproc.COLOR_BGR2YCrCb);

            Mat detect = new Mat();
            List<Mat> channels = new ArrayList<>();
            Core.split(yuvMat,channels);
            LogUtils.e(channels.size());
            Mat outputMark = channels.get(1);
            Imgproc.threshold(outputMark,outputMark,0,255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);
            yuvMat.copyTo(detect,outputMark);

            Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                    new Size(8, 8), new Point(-1, -1));

            Imgproc.dilate(detect,detect,strElement);

            Utils.matToBitmap(detect,bitmap);

            for (Mat mat : channels){
                mat.release();
            }
            yuvMat.release();
            detect.release();
            outputMark.release();
            strElement.release();

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            int []filterPixels = new int[width*height];
            int [] originalPixels = new int[width * height];

            int [] dst = new int[width * height];

            //过滤后皮肤
            bitmap.getPixels(filterPixels, 0, width, 0, 0, width, height);
            originalImage.getPixels(originalPixels, 0, width, 0, 0, width, height);

            for (int i = 0; i < filterPixels.length; i++) {
                int r = Color.red(filterPixels[i]);
                int g = Color.green(filterPixels[i]);
                int b = Color.blue(filterPixels[i]);
                if (Color.rgb(r,g,b)==Color.BLACK){
                    dst[i] = originalPixels[i];
                }
            }
            return Bitmap.createBitmap(dst,width,height, Bitmap.Config.ARGB_8888);
        }return null;
    }

}
