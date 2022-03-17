package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;

import androidx.core.graphics.ColorUtils;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

import static org.opencv.imgproc.Imgproc.MORPH_CROSS;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;

/**
 * Created by YangJianHui on 2021/9/10.
 */
public class FaceOilSecretion extends Filter {


    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){

                Bitmap bitmap = getFaceSkin();

                Mat yuvMat = new Mat();
                Utils.bitmapToMat(bitmap,yuvMat);
                Imgproc.cvtColor(yuvMat,yuvMat,Imgproc.COLOR_RGB2GRAY);

                Mat detect = new Mat();
                List<Mat> channels = new ArrayList<>();
                Core.split(yuvMat,channels);
                Mat outputMark = channels.get(0);
                Imgproc.threshold(outputMark,outputMark,0,255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);
                yuvMat.copyTo(detect,outputMark);
                Utils.matToBitmap(detect,bitmap);

                int width = originalImage.getWidth();
                int height = originalImage.getHeight();

                int [] pixels = new int[width * height];
                int [] dst = new int[width * height];
                int [] original = new int[width * height];

                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                originalImage.getPixels(original, 0, width, 0, 0, width, height);

                int totalGray = 0;
                int count = 0;

                for (int i = 0; i < pixels.length; i++) {
                    int r = Color.red(pixels[i]);
                    int g = Color.green(pixels[i]);
                    int b = Color.blue(pixels[i]);
                    int color = Color.rgb(r,g,b);
                    if (color!=Color.BLACK){
                        int gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                        totalGray +=gray;
                        count ++;
                    }
                }

                int avgGray = (int) (totalGray/count + (totalGray/count * 0.17f));

                for (int i = 0; i < pixels.length; i++) {
                    int r = Color.red(pixels[i]);
                    int g = Color.green(pixels[i]);
                    int b = Color.blue(pixels[i]);
                    int color = Color.rgb(r,g,b);
                    if (color==Color.BLACK) {
                        dst[i] = original[i];
                        continue;
                    }else {
                        int gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                        if (gray>avgGray){
                            r = Color.red(original[i]);
                            g = Color.green(original[i]);
                            b = Color.blue(original[i]);
                            float [] hsl = new float[3];
                            ColorUtils.RGBToHSL(r,g,b,hsl);
                            float newL = hsl[2] + 0.1f;
                            if (newL>=1)newL = 1;
                            hsl[2] = newL;
                            dst[i] = ColorUtils.HSLToColor(hsl);
                        }else {
                            dst[i] = original[i];
                        }
                    }
                }

                filterInfoResult.setFilterBitmap(Bitmap.createBitmap(dst,width,height, Bitmap.Config.ARGB_8888));
                filterInfoResult.setType(FilterType.FACE_OIL_SECRETION);
                filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
            }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }catch (Exception e){
            filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }
        return filterInfoResult;
    }
}

//{
//        int width = originalImage.getWidth();
//        int height = originalImage.getHeight();
//        int count = width * height;
//
//        int [] originalPixels = new int[count];
//        int [] filterPixels = new int[count];
//
//        originalImage.getPixels(originalPixels,0,width,0,0,width,height);
//        int totalGray = 0;
//        for (int i = 0; i <originalPixels.length ; i++) {
//        int originalPixel = originalPixels[i];
//        int R = Color.red(originalPixel);
//        int G = Color.green(originalPixel);
//        int B = Color.blue(originalPixel);
//        int gray = (int) (R * 0.3 + G * 0.59 + B * 0.11);
//        totalGray+=gray;
//        filterPixels[i] = gray;
//        }
//        int avgGray = totalGray / count;
//        double totalCountPixels = 0;
//        double totalWaterPixels = 0;
//        double totalPercentPixels = 0;
//        double hash = 0;
//        for (int i = 0; i <originalPixels.length ; i++) {
//        int gray = filterPixels[i];
//        filterPixels[i] = originalPixels[i];
//        if (gray >= avgGray * 1.1 && gray <= 250) {
//        totalCountPixels++;
//        }
//        if (gray >= avgGray * 1.1 && gray > 250) {
//        totalWaterPixels++;
//        }
//
//        if (gray <avgGray * 1.2){
//
//        }else if (gray>90&&gray<=110){
//
//        }else if (gray>155){
//        totalPercentPixels++;
//        filterPixels[i]=Color.rgb(255, 255, 0);
//        }
//        }
//        totalCountPixels = totalWaterPixels * 60 + totalCountPixels * 2;
//        hash = totalCountPixels / count;
//        LogUtils.e(totalCountPixels,hash, totalPercentPixels,totalPercentPixels / count);
//        int score = 0;
//        score = (int) ((30)+ ((1-hash)*0.9) * 40);
//        LogUtils.e(score);
//
//        if (score<=50&&score>55){
//        score = (score-40) * 21 / 10 +40;
//        }else if (score<=55){
//        score = (score-40) * 3 +25;
//        }
//        if (score > 80) {
//        score = 78;
//        }
//        if (score<35){
//        score =35;
//        }
//
//        Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(filterPixels,0,width, 0, 0, width, height);
//        filterInfoResult.setResistance(getResistance());
//        filterInfoResult.setScore(score);
//        filterInfoResult.setRatio((totalPercentPixels * 100 / count));
//        filterInfoResult.setDepth(0);
//        filterInfoResult.setFilterBitmap(bitmap);
//        filterInfoResult.setType(FilterType.SKIN_OIL_SECRETION);
//        filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
//        }