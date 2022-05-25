package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/28.
 */
public class FaceDarkCircles extends Filter implements FaceFilter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap filterBitmap = getFacePartImage();

            Mat filterMat = new Mat();
            Utils.bitmapToMat(filterBitmap,filterMat);

            Mat oriMat = new Mat();
            Utils.bitmapToMat(filterBitmap,oriMat);

            Imgproc.cvtColor(filterMat,filterMat,Imgproc.COLOR_RGB2HSV);
            Imgproc.cvtColor(filterMat,filterMat,Imgproc.COLOR_RGB2HSV);

            Mat dst = new Mat();

            Core.inRange(filterMat,new Scalar(35,43,46),
                    new Scalar(77,255,255),dst);
//
            Core.bitwise_not(dst,dst);
//
            Mat and = new Mat();

            Core.bitwise_and(oriMat,oriMat,and,dst);
//
            Mat yCrCb = new Mat();
//
            Imgproc.cvtColor(and,yCrCb,Imgproc.COLOR_RGB2HSV);
            Imgproc.cvtColor(yCrCb,yCrCb,Imgproc.COLOR_RGB2HSV);
//
            Core.inRange(yCrCb,new Scalar(78,43,46),
                    new Scalar(99,255,255),yCrCb);

            Utils.matToBitmap(yCrCb,filterBitmap);


            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            int [] originalPixels = new int[width * height];
            int [] filterPixels = new int[width*height];
            int [] resultPixels = new int[width*height];


            originalImage.getPixels(originalPixels, 0, width, 0, 0, width, height);
            filterBitmap.getPixels(filterPixels, 0, width, 0, 0, width, height);

            for (int i = 0; i < filterPixels.length; i++) {
               if (filterPixels[i]==Color.BLACK){
                  resultPixels[i] = originalPixels[i];
               }else {
                  double [] lab = new double[3];
                  ColorUtils.colorToLAB(originalPixels[i],lab);
                  lab[0] = lab[0] - (lab[0] * 0.2f);
                  resultPixels[i] = ColorUtils.LABToColor(lab[0],lab[1],lab[2]);
               }
            }
            filterBitmap.setPixels(resultPixels,0,width, 0, 0, width, height);


            filterInfoResult.setFilterBitmap(filterBitmap);
            filterInfoResult.setType(FilterType.Face_Dark_Circles);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

   @Override
   public FacePart[] getFacePart() {
      return new FacePart[]{FacePart.FACE_EYE_BOTTOM};
   }
}
