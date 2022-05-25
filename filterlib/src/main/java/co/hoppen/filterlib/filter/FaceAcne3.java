package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/28.
 */
public class FaceAcne3 extends Filter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap copyOriginal = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat filterMat = new Mat();
            Utils.bitmapToMat(copyOriginal,filterMat);


//            Mat gaussian = new Mat();
//            Mat ums = new Mat();
//            Imgproc.GaussianBlur(filterMat,gaussian,new Size(0,0),20);
//            Core.addWeighted(filterMat,-4.0,gaussian,5.0,0.0,ums);
//
//            Imgproc.cvtColor(ums,ums,Imgproc.COLOR_RGB2HSV);
//
//            Core.inRange(ums,new Scalar(0,0,221),new Scalar(180,30,255),ums);
//
//            Utils.matToBitmap(ums,copyOriginal);
//
//            int width = copyOriginal.getWidth();
//            int height = copyOriginal.getHeight();
//            int [] filterPixels = new int[width * height];
//            copyOriginal.getPixels(filterPixels,0,width,0,0,width,height);
//            for (int i = 0; i < filterPixels.length; i++) {
//               int pixels = filterPixels[i];
//               if (Color.BLACK==pixels){
//                  filterPixels[i] = 0x00000000;
//               }
//            }
//            Bitmap basemapBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
//            basemapBitmap.setPixels(filterPixels,0,width, 0, 0, width, height);
//
//            Bitmap resultBitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);
//            Canvas canvas = new Canvas(resultBitmap);
//            canvas.drawBitmap(basemapBitmap,0,0,null);


            filterInfoResult.setFilterBitmap(getFaceSkinByRgb());
            filterInfoResult.setType(FilterType.FACE_ACNE);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         LogUtils.e(e.toString());
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

   private Bitmap getFaceSkinByRgb(){
      Bitmap originalImage = getOriginalImage();
      Mat oriMat = new Mat();
      Utils.bitmapToMat(originalImage,oriMat);
      Mat hsvMat = new Mat();
      Imgproc.cvtColor(oriMat,hsvMat,Imgproc.COLOR_RGB2HSV);

      Mat ycrCbMat = new Mat();
      Imgproc.cvtColor(oriMat,ycrCbMat,Imgproc.COLOR_RGB2YCrCb);

      Core.inRange(hsvMat,new Scalar(0,15,0),new Scalar(17,170,255),hsvMat);

      Core.inRange(ycrCbMat,new Scalar(0,180,85),new Scalar(255,180,135),ycrCbMat);

      Core.add(hsvMat,ycrCbMat,oriMat);

      Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(3,3));
      //关操作,先膨胀后腐蚀
      Imgproc.morphologyEx(oriMat,oriMat,Imgproc.MORPH_DILATE,kernel);

      kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(13,13));

      Imgproc.morphologyEx(oriMat,oriMat,Imgproc.MORPH_ERODE,kernel);


      Bitmap skinBitmap = Bitmap.createBitmap(originalImage.getWidth(),originalImage.getHeight(),originalImage.getConfig());
      Utils.matToBitmap(oriMat,skinBitmap);

      oriMat.release();
      hsvMat.release();
      ycrCbMat.release();
      return skinBitmap;
   }
}
