package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/28.
 */
public class FaceAcne extends Filter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap copyOriginal = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat filterMat = new Mat();
            Utils.bitmapToMat(copyOriginal,filterMat);

            Mat oriMat = new Mat();
            Utils.bitmapToMat(copyOriginal,oriMat);

            //Core.multiply(pre,Scalar(contrast / 100,contrast / 100,contrast / 100,contrast / 100),            dst        )

            Imgproc.cvtColor(filterMat, filterMat, Imgproc.COLOR_RGB2HSV);

            double originBrightness = Core.mean(filterMat).val[0];
            //LogUtils.e(originBrightness);

            //Core.add(filterMat, new Scalar(originBrightness-originBrightness, originBrightness-originBrightness, originBrightness-originBrightness),filterMat);

            //Core.multiply(filterMat,new Scalar(70d/100d,70d/100d,70d/100d,70d/100d),filterMat);

            //Imgproc.cvtColor(filterMat,filterMat,Imgproc.COLOR_RGB2HSV);
//
//            List<Mat> channels = new ArrayList<>();
//            Core.split(filterMat,channels);
//            filterMat = channels.get(1);


//            LogUtils.e(channels.size());
//            Mat outputMark = channels.get(1);

//            Mat kernel = new Mat(3,3, CvType.CV_32FC1);
//            float [] k = {0, -1, 0, -1, 5, -1, 0, -1, 0};
//            kernel.put(0,0,k);
//            Imgproc.filter2D(filterMat,filterMat,-1,kernel);
//            Imgproc.cvtColor(filterMat,filterMat,Imgproc.COLOR_RGB2HSV);

            Mat darkMat = new Mat();
            Mat lightMat = new Mat();
            Core.inRange(filterMat,new Scalar(0,43,46),new Scalar(10,255,255),darkMat);
            Core.inRange(filterMat,new Scalar(156,43,46),new Scalar(180,255,255),lightMat);

            Core.add(darkMat,lightMat,filterMat);

            Imgproc.threshold(filterMat,filterMat,5,255,Imgproc.THRESH_BINARY_INV);// | Imgproc.THRESH_OTSU

            Imgproc.medianBlur(filterMat,filterMat,5);

            Imgproc.dilate(filterMat,filterMat,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(5,5)));

//            Imgproc.blur(filterMat,filterMat,new Size(9,9));

            List<MatOfPoint> list = new ArrayList<>();

            Mat hierarchy = new Mat();
            Imgproc.findContours(filterMat,list,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);

            LogUtils.e(list.size());

            for (int i = 0; i < list.size(); i++) {
               MatOfPoint point = list.get(i);
               LogUtils.e(point.size().area());
               if (point.size().area()<=300&&point.size().area()>=10){
                  Imgproc.drawContours(oriMat,list,i,new Scalar(255,0,0,255));
               }
            }

            Utils.matToBitmap(oriMat,copyOriginal);
            filterInfoResult.setFilterBitmap(copyOriginal);
            filterInfoResult.setType(FilterType.FACE_ACNE);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }
}
