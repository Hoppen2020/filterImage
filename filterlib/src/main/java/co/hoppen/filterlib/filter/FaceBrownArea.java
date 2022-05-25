package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.lang.annotation.Native;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/12.
 */
public class FaceBrownArea extends Filter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat oriMat = new Mat();
            Utils.bitmapToMat(bitmap,oriMat);
            Imgproc.cvtColor(oriMat,oriMat,Imgproc.COLOR_RGBA2RGB);


            Mat lab = new Mat();
            Imgproc.cvtColor(oriMat,lab,Imgproc.COLOR_RGB2Lab);
            //lab.convertTo(lab,CvType.CV_32F,1/255f);


            Mat hsv = new Mat();
            Imgproc.cvtColor(oriMat,hsv,Imgproc.COLOR_RGB2HSV);
            //hsv.convertTo(hsv,CvType.CV_32F,1/255f);


            List<Mat> splitList = new ArrayList<>();
            Core.split(lab,splitList);
            Mat matB = splitList.get(2);
            //对比度增强

            Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(matB);
            LogUtils.e(minMaxLocResult.maxVal,minMaxLocResult.minVal,minMaxLocResult.minLoc,minMaxLocResult.maxLoc,minMaxLocResult.toString());

//            Core.convertScaleAbs(matB,matB,1.5);


            Mat matB32 = new Mat();
            matB.convertTo(matB32,CvType.CV_32FC1,1/255f);
            LogUtils.e(matB32.toString());


            Mat sortMat = new Mat();
            Core.sort(matB32,sortMat,Core.SORT_ASCENDING);

            double percent = 0.0;
            double[] min = sortMat.get(0,0);
            double[] max = sortMat.get(sortMat.rows()-1, sortMat.cols()-1);

            LogUtils.e(Arrays.toString(min),Arrays.toString(max));

//            for (int i = 0; i < matB32.cols(); i++) {
//               for (int j = 0; j < matB32.rows(); j++) {
//                  double[] doubles = matB32.get(i, j);
//                  int pos = i * matB32.rows() + j;
//                   sort [pos] = (float) doubles[0];
//               }
//            }
//            Imgproc.



            splitList.clear();
            Core.split(hsv,splitList);
            LogUtils.e(splitList.size());
            Mat matV = splitList.get(2);

            Mat mat = new Mat();
            splitList.clear();
            splitList.add(new Mat(oriMat.rows(),oriMat.cols(),CvType.CV_8UC1));
            splitList.add(matB);
            splitList.add(matV);
            Core.merge(splitList,mat);
            Imgproc.cvtColor(mat, mat,Imgproc.COLOR_HSV2RGB);


//            matB32.convertTo(matB32,CvType.CV_8UC1,255/255);

//            Core.bitwise_not(mat,mat);


            Imgproc.resize(oriMat,oriMat,new Size(oriMat.width(),oriMat.height()),20,20,Imgproc.INTER_CUBIC);


            Utils.matToBitmap(oriMat,bitmap);

            filterInfoResult.setFilterBitmap(bitmap);
            filterInfoResult.setType(FilterType.FACE_BROWN_AREA);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

   //solveCubic 三次样条插值

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
