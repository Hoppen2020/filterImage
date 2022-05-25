package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/12.
 */
public class FaceTest extends Filter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat oriMat = new Mat();
            Utils.bitmapToMat(bitmap,oriMat);
            Photo.detailEnhance(oriMat,oriMat);
            Imgproc.cvtColor(oriMat,oriMat,Imgproc.COLOR_RGBA2GRAY);

            Mat grayMat = new Mat();
            //Imgproc.cvtColor(oriMat,grayMat,Imgproc.COLOR_RGB2GRAY);

            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,new Size(21,21));
            Imgproc.morphologyEx(oriMat,oriMat,Imgproc.MORPH_BLACKHAT,kernel);
//            Imgproc.cvtColor(oriMat,oriMat,Imgproc.COLOR_RGBA2GRAY);
            Imgproc.threshold(oriMat,oriMat,10,255,Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);

            Mat result = new Mat();
            Photo.inpaint(oriMat,oriMat,result,1,Photo.INPAINT_TELEA);

            Utils.matToBitmap(result,bitmap);

            filterInfoResult.setFilterBitmap(bitmap);
            filterInfoResult.setType(FilterType.FACE_TEST);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

//毛发去除 method one
//   Mat oriMat = new Mat();
//            Utils.bitmapToMat(bitmap,oriMat);
//            Imgproc.cvtColor(oriMat,oriMat,Imgproc.COLOR_RGBA2RGB);
//
//   Mat grayMat = new Mat();
//            Imgproc.cvtColor(oriMat,grayMat,Imgproc.COLOR_RGB2GRAY);
//
//   Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS,new Size(21,21));
//            Imgproc.morphologyEx(grayMat,grayMat,Imgproc.MORPH_BLACKHAT,kernel);
//
//            Imgproc.threshold(grayMat,grayMat,10,255,Imgproc.THRESH_BINARY);
//
//   Mat result = new Mat();
//            Photo.inpaint(oriMat,grayMat,result,1,Photo.INPAINT_TELEA);
//            Photo.detailEnhance(oriMat,oriMat);
//
//            Utils.matToBitmap(result,bitmap);

}
