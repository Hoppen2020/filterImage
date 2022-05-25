package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.Imgproc;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/28.
 */
public class FaceAcne4 extends Filter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap copyOriginal =
//                    getFaceSkinByRgb();
                    originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat filterMat = new Mat();
            Utils.bitmapToMat(copyOriginal,filterMat);


            SimpleBlobDetector_Params params = new SimpleBlobDetector_Params();
            //最小阈值
            params.set_minThreshold(20);
            //最大阈值
            params.set_maxThreshold(200);
            //区块过滤器
            params.set_filterByArea(true);
            //最小区块
            params.set_minArea(15);
            //圆过滤器
            params.set_filterByCircularity(true);
            //最小圆
            params.set_minCircularity(0.5f);//0.7
            //凸度过滤器
            params.set_filterByConvexity(true);
            //最小凸度
            params.set_minConvexity(0.5f);//0.87
            //偏心率过滤器
            params.set_filterByInertia(false);
            //最小偏心率
            params.set_minInertiaRatio(0.5f);

            MatOfKeyPoint keyPoints = new MatOfKeyPoint();
            SimpleBlobDetector detector = SimpleBlobDetector.create(params);
            Mat mask = getFaceSkinByRgb();
            detector.detect(filterMat,keyPoints,mask);

            Mat dst = new Mat();
            Features2d.drawKeypoints(filterMat,keyPoints,dst, new Scalar(0,200,200),Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS);

            Utils.matToBitmap(dst,copyOriginal);


            filterInfoResult.setFilterBitmap(copyOriginal);
            filterInfoResult.setType(FilterType.FACE_ACNE);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         LogUtils.e(e.toString());
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

   private Mat getFaceSkinByRgb(){
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

      kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,new Size(6,6));

      Imgproc.morphologyEx(oriMat,oriMat,Imgproc.MORPH_ERODE,kernel);


      //Bitmap skinBitmap = Bitmap.createBitmap(originalImage.getWidth(),originalImage.getHeight(),originalImage.getConfig());
      //Utils.matToBitmap(oriMat,skinBitmap);

      hsvMat.release();
      ycrCbMat.release();
      return oriMat;
   }


}
