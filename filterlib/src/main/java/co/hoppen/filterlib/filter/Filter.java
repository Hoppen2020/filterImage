package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.ColorRes;
import androidx.core.graphics.ColorUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.utils.CutoutUtils;

/**
 * Created by YangJianHui on 2021/9/10.
 */
public abstract class Filter {
    private Bitmap originalImage;
    private float resistance;
    private Bitmap facePartImage;

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
            Mat outputMark = channels.get(0);
            Imgproc.threshold(outputMark,outputMark,0,255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);
            yuvMat.copyTo(detect,outputMark);

            Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                    new Size(8, 8), new Point(-1, -1));

            //

            Imgproc.cvtColor(detect,detect,Imgproc.COLOR_YCrCb2RGB);
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

            int [] filterPixels = new int[width*height];
            int [] originalPixels = new int[width * height];

            int [] dst = new int[width * height];

            //???????????????
            bitmap.getPixels(filterPixels, 0, width, 0, 0, width, height);
            originalImage.getPixels(originalPixels, 0, width, 0, 0, width, height);


            float [] s = new float[3];
            ColorUtils.colorToHSL(Color.rgb(155,152,83),s);
            for (int i = 0; i < filterPixels.length; i++) {
                int r = Color.red(filterPixels[i]);
                int g = Color.green(filterPixels[i]);
                int b = Color.blue(filterPixels[i]);
                if (Color.rgb(r,g,b)==Color.rgb(0,135,0)){
                    dst[i] = 0x00000000;
                    continue;
                }else {
                    float [] hsb = rgb2hsb(r,g,b);
                    if (hsb[1]<0.65f && hsb[1]>0.25f&&hsb[0]>=42&&hsb[0]<70){
                        dst[i] = 0x00000000;
                    }else {
                        dst[i] = originalPixels[i];
                    }
                }
            }


            Bitmap create = Bitmap.createBitmap(dst, width, height, Bitmap.Config.ARGB_8888);
            Mat result = new Mat();
            Utils.bitmapToMat(create,result);
            Imgproc.cvtColor(result,result,Imgproc.COLOR_RGB2GRAY);
            Imgproc.GaussianBlur(result, result, new Size(7,7), 0);

            Imgproc.threshold(result, result, 100, 255, Imgproc.THRESH_BINARY_INV);
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                    new Size(6, 6), new Point(-1, -1));
            Imgproc.morphologyEx(result,result,Imgproc.MORPH_CLOSE,kernel);

            Utils.matToBitmap(result,create);
            result.release();
            kernel.release();

            create.getPixels(filterPixels, 0, width, 0, 0, width, height);

            for (int i = 0; i <filterPixels.length; i++) {
                dst[i] = 0x00000000;
                int r = Color.red(filterPixels[i]);
                int g = Color.green(filterPixels[i]);
                int b = Color.blue(filterPixels[i]);
                if (Color.rgb(r,g,b)==Color.BLACK){
                    dst[i] = originalPixels[i];
                }
            }
            create = Bitmap.createBitmap(dst, width, height, Bitmap.Config.ARGB_8888);
            return create;
        }return null;
    }

    /**
     * src?????????????????????alpha?????????0
     * @param src
     * @return
     */
    public int avgGray(Bitmap src){
        int avgGray = 0;
        if (src!=null){
            int count = 0;
            int width = src.getWidth();
            int height = src.getHeight();
           // int size = width * height;
            int pixels [] = new int[width * height];
            src.getPixels(pixels, 0, width, 0, 0, width, height);
            int totalGray = 0;
            for (int i = 0; i < pixels.length; i++) {
                if (Color.alpha(pixels[i])==0){
                    continue;
                }
                int r = Color.red(pixels[i]);
                int g = Color.green(pixels[i]);
                int b = Color.blue(pixels[i]);
                int gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                totalGray += gray;
                count++;
            }
            if (totalGray!=0 && count!=0){
                avgGray = totalGray / count;
            }
            LogUtils.e(avgGray,totalGray);
        }

        return avgGray;
    }

    public float[] rgb2hsb(int rgbR, int rgbG, int rgbB) {
        assert 0 <= rgbR && rgbR <= 255;
        assert 0 <= rgbG && rgbG <= 255;
        assert 0 <= rgbB && rgbB <= 255;
        int[] rgb = new int[]{ rgbR, rgbG, rgbB };
        Arrays.sort(rgb);
        int max = rgb[ 2 ];
        int min = rgb[ 0 ];

        float hsbB = max / 255.0f;
        float hsbS = max == 0 ? 0 : (max - min) / (float) max;

        float hsbH = 0;
        if (max == rgbR && rgbG >= rgbB) {
            hsbH = (rgbG - rgbB) * 60f / (max - min) + 0;
        } else if (max == rgbR && rgbG < rgbB) {
            hsbH = (rgbG - rgbB) * 60f / (max - min) + 360;
        } else if (max == rgbG) {
            hsbH = (rgbB - rgbR) * 60f / (max - min) + 120;
        } else if (max == rgbB) {
            hsbH = (rgbR - rgbG) * 60f / (max - min) + 240;
        }

        return new float[]{ hsbH, hsbS, hsbB };
    }

    protected Bitmap getRgbSkin(){
        if (originalImage!=null){
            Bitmap copy = originalImage.copy(Bitmap.Config.ARGB_8888, true);
            Mat oriMat = new Mat();
            Utils.bitmapToMat(copy,oriMat);

            Mat hsvMat = new Mat();
            Imgproc.cvtColor(oriMat,hsvMat,Imgproc.COLOR_RGB2HSV);
            Core.inRange(hsvMat,new Scalar(0,15,0),new Scalar(17,170,255),hsvMat);

            Mat yCrBrMat = new Mat();
            Imgproc.cvtColor(oriMat,yCrBrMat,Imgproc.COLOR_RGB2YCrCb);
            Core.inRange(yCrBrMat,new Scalar(0,135,85),new Scalar(255,180,135),yCrBrMat);

            Mat mask = new Mat();
            Core.bitwise_and(hsvMat,yCrBrMat,mask);

            Mat result = new Mat();
            Core.bitwise_and(oriMat,oriMat,result,mask);
            Utils.matToBitmap(result,copy);

            oriMat.release();
            hsvMat.release();
            yCrBrMat.release();
            mask.release();
            result.release();
            return copy;
        }
        return null;
    }

    private void setFacePartImage(Bitmap facePartImage) {
        this.facePartImage = facePartImage;
    }

    public Bitmap getFacePartImage() {
        return facePartImage;
    }

    public boolean facePositioning(MLFaceAnalyzer analyzer){
        try {
            MLFace result = null;
            MLFrame frame = MLFrame.fromBitmap(originalImage);
            SparseArray<MLFace> mlFaceSparseArray = analyzer.analyseFrame(frame);
            if (mlFaceSparseArray.size()>0){
                result = mlFaceSparseArray.get(0);
                String face = GsonUtils.toJson(result);
                SPUtils.getInstance().put("face",face);
//                LogUtils.e(face);
                //GsonUtils.fromJson(result,GsonUtils.getType(Result.class, GsonUtils.getListType(Person.class));
            }else {
                String face = SPUtils.getInstance().getString("face");
                result = GsonUtils.fromJson(face,GsonUtils.getType(MLFace.class));
            }
            analyzer.stop();
            if (result!=null){
                setFacePartImage(CutoutUtils.cutoutPart(originalImage,((FaceFilter) this).getFacePart(),result));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
