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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/12.
 */
public class FaceSensitive extends Filter{


   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            Mat inRangeMat = new Mat();
            Bitmap rangeBitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);
            Utils.bitmapToMat(rangeBitmap,inRangeMat);
            Imgproc.cvtColor(inRangeMat,inRangeMat,Imgproc.COLOR_RGB2HSV);
            Core.inRange(inRangeMat,new Scalar(78,43,46),new Scalar(99,255,255),inRangeMat);
            //Bitmap inRangeBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(inRangeMat,rangeBitmap);
            inRangeMat.release();

            Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat oriMat = new Mat();
            Utils.bitmapToMat(bitmap,oriMat);
            // R G B A
            List<Mat> rgbList = new ArrayList<>();
            //通道分离
            Core.split(oriMat,rgbList);
            Mat splitR = rgbList.get(0);
            //Mat splitG = rgbList.get(1);
            //Mat splitB = rgbList.get(2);

            Mat detect = new Mat();

            rgbList.clear();
            rgbList.add(splitR);
            rgbList.add(new Mat(oriMat.rows(),oriMat.cols(),CvType.CV_8UC1));
            rgbList.add(new Mat(oriMat.rows(),oriMat.cols(),CvType.CV_8UC1));
            Core.merge(rgbList,detect);


            Imgproc.cvtColor(detect,detect,Imgproc.COLOR_RGBA2GRAY);//COLOR_RGBA2GRAY
//            Core.pow(detect,0.5,detect);
            Utils.matToBitmap(detect,bitmap);
            oriMat.release();
            splitR.release();
            detect.release();


            int avgGray = 0;
            int totalGray = 0;
            int count = 0;


            int [] filterPixels = new int[width * height];
            int [] originalPixels = new int[width * height];
            int [] dstPixels = new int[width * height];
            int [] isRangePixels = new int[width * height];

            bitmap.getPixels(filterPixels, 0, width, 0, 0, width, height);
            originalImage.getPixels(originalPixels, 0, width, 0, 0, width, height);
            rangeBitmap.getPixels(isRangePixels, 0, width, 0, 0, width, height);

            for (int i = 0; i < filterPixels.length; i++) {
               if (Color.alpha(originalPixels[i])==0|| Color.red(filterPixels[i])==0){
                  continue;
               }
                  int r =Color.red(filterPixels[i]);
                  totalGray += r;
                  count++;
            }
            avgGray = totalGray /count;

            //cache data
            float [] hsl = new float[3];
            int a = 0;

            for (int i = 0; i < filterPixels.length; i++) {
               if (Color.alpha(originalPixels[i])==0){
                  dstPixels[i] = 0x00000000;
                  continue;
               }
               if (Color.WHITE==isRangePixels[i]){
                  dstPixels[i] =
//                          0x00000000;
                          originalPixels[i];
                  continue;
               }
               int r =Color.red(filterPixels[i]);
               if (r<avgGray){
                  dstPixels[i] =
//                          0x00000000;
                          originalPixels[i];
               }else {
                  int oR = Color.red(originalPixels[i]);
                  int oG = Color.green(originalPixels[i]);
                  int oB = Color.blue(originalPixels[i]);

                  int filterColor = Color.rgb(oR,oG,oB);

                  ColorUtils.colorToHSL(filterColor,hsl);

                  if (a==0){
                     LogUtils.e(Arrays.toString(hsl));
                     a++;
                  }
                  //(hsl[0]<=35 ||hsl[0]>=330) && hsl[2] >0.48f&& hsl[2] <=0.65f&& hsl[1] >= 0.08f
                  if ((hsl[0]<=35 ||hsl[0]>=330) && hsl[2] >0.4f&& hsl[2] <=0.7f&& hsl[1] >= 0.09f){//&&hsl[2]>0.45f
                     oB = (int) (oB + (oB*0.8f));
                     if (oB>=255) oB = 255;
                     filterColor = Color.rgb(oB,oG,oR);
                  }else {
//                     filterColor =
//                             0x00000000;
                  }
                  dstPixels[i] = filterColor;
               }
            }


            Bitmap createBitmap = Bitmap.createBitmap(dstPixels, width, height, Bitmap.Config.ARGB_8888);

//            Mat result = new Mat();
//            Utils.bitmapToMat(createBitmap,result);
//            Imgproc.cvtColor(result,result, Imgproc.COLOR_RGBA2GRAY);
//            //opencv hsv 模式范围H[0...180] S[0...255] V[0...255]
//            Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
//                    new Size(8, 8), new Point(-1, -1));
//            Imgproc.erode(result,result,strElement);
//
//            Utils.matToBitmap(result,createBitmap);

            filterInfoResult.setFilterBitmap(createBitmap);
            filterInfoResult.setType(FilterType.FACE_SENSITIVE);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

}
