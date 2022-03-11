package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/7.
 */
public class FaceSkinWrinkle extends Filter{

    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){
                Bitmap bitmap = getFaceSkin();
                Mat grayMat = new Mat();
                Utils.bitmapToMat(bitmap,grayMat);
                Imgproc.cvtColor(grayMat,grayMat,Imgproc.COLOR_RGB2GRAY);
                Mat result = new Mat();
                Imgproc.Sobel(grayMat,result,-1,0,1,9);
                Core.convertScaleAbs(result, result);
                Utils.matToBitmap(result,bitmap);

                int height = originalImage.getHeight();
                int width = originalImage.getWidth();

                int [] pixels = new int[width * height];
                int [] dstPixels = new int[width * height];

                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                originalImage.getPixels(dstPixels, 0, width, 0, 0, width, height);
                for (int i = 0; i < pixels.length; i++) {
                    int pixel = pixels[i];
                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);
                    if (Color.rgb(r,g,b)==Color.BLACK) {
                        if (Color.alpha(dstPixels[i])!=0){
                            dstPixels[i] = Color.rgb(205,183,158);
                        }
                    }
                }
                Bitmap resultBitmap = Bitmap.createBitmap(dstPixels, width, height, Bitmap.Config.ARGB_8888);
                filterInfoResult.setFilterBitmap(resultBitmap);
                filterInfoResult.setType(FilterType.FOLLICLE_CLEAN_DEGREE);
                filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
            }else{
                filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
            filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }
        return filterInfoResult;
    }
}