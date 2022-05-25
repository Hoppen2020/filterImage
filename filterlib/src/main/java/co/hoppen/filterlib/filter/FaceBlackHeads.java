package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/28.
 */
public class FaceBlackHeads extends Filter implements FaceFilter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap operateBitmap = getFacePartImage();
//                    originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat resultMat = new Mat();
            Utils.bitmapToMat(operateBitmap,resultMat);

            SimpleBlobDetector_Params params = new SimpleBlobDetector_Params();
            //最小阈值
            params.set_minThreshold(50f);
            //最大阈值
            params.set_maxThreshold(255f);
            //区块过滤器
            params.set_filterByArea(true);
            //最小区块
            params.set_minArea(5);
            //圆过滤器
            params.set_filterByCircularity(true);
            //最小圆
            params.set_minCircularity(0.5f);//0.7
            //凸度过滤器
            params.set_filterByConvexity(true);
            //最小凸度
            params.set_minConvexity(0.65f);//0.87
            //偏心率过滤器
            params.set_filterByInertia(false);
            //最小偏心率
            params.set_minInertiaRatio(0.5f);
            MatOfKeyPoint keyPoints = new MatOfKeyPoint();
            SimpleBlobDetector detector = SimpleBlobDetector.create(params);
            detector.detect(resultMat,keyPoints);

//            LogUtils.e(keyPoints.toList().size());
            List<KeyPoint> keyPointsList = keyPoints.toList();


            Mat dst = new Mat();
            Features2d.drawKeypoints(resultMat,keyPoints,dst, new Scalar(0,200,200),Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS);

            Utils.matToBitmap(dst,operateBitmap);


            Bitmap resultBitmap = getOriginalImage().copy(getOriginalImage().getConfig(),true);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(operateBitmap,0,0,null);


            filterInfoResult.setFilterBitmap(resultBitmap);
            filterInfoResult.setType(FilterType.FACE_BLACK_HEADS);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

   @Override
   public FacePart[] getFacePart() {
      return new FacePart[]{FacePart.FACE_NOSE};
   }

}
