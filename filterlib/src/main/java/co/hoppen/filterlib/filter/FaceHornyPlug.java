package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/12.
 */
public class FaceHornyPlug extends Filter implements FaceFilter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){

            Bitmap bitmap = getFacePartImage();
//                    originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat oriMat = new Mat();
            Utils.bitmapToMat(bitmap,oriMat);

            Mat filterMat = new Mat();
            Core.bitwise_not(oriMat,filterMat);
            Imgproc.cvtColor(filterMat,filterMat,Imgproc.COLOR_RGBA2GRAY);


            Core.inRange(filterMat,new Scalar(180,180,180),new Scalar(210,210,210),oriMat);

            Utils.matToBitmap(oriMat,bitmap);

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();
            int [] pixels = new int[width * height];
            int [] dstPixels = new int[width * height];
            int [] originalPixels = new int[width * height];
            originalImage.getPixels(originalPixels, 0, width, 0, 0, width, height);
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);


            for (int i = 0; i < pixels.length; i++) {
               int alpha = Color.alpha(originalPixels[i]);

               if (alpha==0){
                  dstPixels[i] = 0x00000000;
                  continue;
               }

               int r = Color.red(pixels[i]);
               int g = Color.green(pixels[i]);
               int b = Color.blue(pixels[i]);
               if (Color.rgb(r,g,b) == Color.WHITE){
                  dstPixels[i] = Color.rgb(255,255,0);
               }else dstPixels[i] = originalPixels[i];
            }

            filterInfoResult.setFilterBitmap(Bitmap.createBitmap(dstPixels,width,height, Bitmap.Config.ARGB_8888));
            filterInfoResult.setType(FilterType.FACE_HORNY_PLUG);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

   @Override
   public FacePart[] getFacePart() {
      return new FacePart[]{FacePart.FACE_T,FacePart.FACE_LEFT_RIGHT_AREA};
   }
}
