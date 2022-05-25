package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/8.
 */
public class FaceFollicleCleanDegree extends Filter implements FaceFilter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap createBitmap = getFacePartImage();
                    //originalImage.copy(Bitmap.Config.ARGB_8888,true);
            Mat filterMat = new Mat();
            Utils.bitmapToMat(createBitmap,filterMat);

            Mat oriMat = new Mat();
            Utils.bitmapToMat(createBitmap,oriMat);

            List<Mat> rgbList = new ArrayList<>();
            Core.split(filterMat,rgbList);

            Mat blueMat = rgbList.get(2);

            Imgproc.blur(blueMat,filterMat,new Size(5,5));

            Imgproc.adaptiveThreshold(filterMat,filterMat,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,7,2);

            Imgproc.medianBlur(filterMat,filterMat,5);

            Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));

            Imgproc.erode(filterMat,filterMat,structuringElement);

            Imgproc.dilate(filterMat,filterMat,structuringElement);

            List<MatOfPoint> list = new ArrayList<>();

            Imgproc.findContours(filterMat,list,new Mat(), Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);

            LogUtils.e(list.size());

            for (int i = 0; i < list.size(); i++) {
               MatOfPoint point = list.get(i);
               if (point.size().area()<20){
                  Imgproc.drawContours(oriMat,list,i,new Scalar(255,0,0,255));
               }
            }

            Utils.matToBitmap(oriMat,createBitmap);
            filterMat.release();
            oriMat.release();
            blueMat.release();
            structuringElement.release();

            Bitmap resultBitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(createBitmap,0,0,null);

            if (!createBitmap.isRecycled())createBitmap.recycle();

            filterInfoResult.setFilterBitmap(resultBitmap);
            filterInfoResult.setType(FilterType.FACE_FOLLICLE_CLEAN_DEGREE);
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

   @Override
   public FacePart[] getFacePart() {
      return new FacePart[]{FacePart.FACE_MIDDLE};
   }
}
