package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/12.
 */
public class FaceSuperficialPlaque extends Filter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){

            Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat oriMat = new Mat();
            Utils.bitmapToMat(bitmap,oriMat);

            Mat grayMat = new Mat();
            Imgproc.cvtColor(oriMat,grayMat,Imgproc.COLOR_RGBA2GRAY);

            Core.inRange(grayMat,new Scalar(3,3,3),new Scalar(14,14,14),grayMat);

            Utils.matToBitmap(grayMat,bitmap);


            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int []filterPixels = new int[width * height];
            int []originalPixels = new int[width*height];
            int []dstPixels = new int[width * height];
            bitmap.getPixels(filterPixels, 0, width, 0, 0, width, height);
            originalImage.getPixels(originalPixels, 0, width, 0, 0, width, height);

            for (int i = 0; i <filterPixels.length ; i++) {
               if (Color.alpha(originalPixels[i])==0){
                  dstPixels[i] = 0x00000000;
                  continue;
               }
               int r = Color.red(filterPixels[i]);
               int g = Color.green(filterPixels[i]);
               int b = Color.blue(filterPixels[i]);
               if (Color.rgb(r,g,b)==Color.WHITE){
                  dstPixels[i] = Color.rgb(255,26,26);
               }else{
                  dstPixels[i] = originalPixels[i];
               }
            }
            bitmap = Bitmap.createBitmap(dstPixels,width,height, Bitmap.Config.ARGB_8888);

            filterInfoResult.setFilterBitmap(bitmap);
            filterInfoResult.setType(FilterType.FACE_SUPERFICIAL_PLAQUE);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

}
