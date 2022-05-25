package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Algorithm;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/28.
 */
public class FaceAcne2 extends Filter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap copyOriginal = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat filterMat = new Mat();
            Utils.bitmapToMat(copyOriginal,filterMat);

//            double originBrightness  = Core.mean(filterMat).val[0];
//            LogUtils.e(originBrightness);
//
//            double brightness = 120;
//            //增加亮度
//            Core.add(filterMat,new Scalar(brightness-originBrightness,brightness-originBrightness,brightness-originBrightness),filterMat);
//
//            double contrast = 160;
//            //增加对比度
//            Core.multiply(filterMat,new Scalar(contrast/100d,contrast/100d,contrast/100d,contrast/100d),filterMat);
//
//            Imgproc.cvtColor(filterMat,filterMat,Imgproc.COLOR_RGB2GRAY);
//
//
//            Mat xGray = new Mat();
//            Mat yGray = new Mat();
//            Imgproc.Sobel(filterMat,xGray,filterMat.depth(),1,0);
//            Imgproc.Sobel(filterMat,yGray,filterMat.depth(),0,1);
//
//            Core.add(xGray,yGray,filterMat);
//
//            Imgproc.GaussianBlur(filterMat,filterMat,new Size(3,3),0,0);
//
//            Mat labels = new Mat();
//            Mat starts = new Mat();
//            Mat centroids = new Mat();
//            int count = Imgproc.connectedComponentsWithStats(filterMat, labels, starts, centroids);
//            LogUtils.e(count);
//            Photo.colorChange(filterMat,filterMat,filterMat,70);

            Mat gaussian = new Mat();
            Mat ums = new Mat();
            Imgproc.GaussianBlur(filterMat,gaussian,new Size(0,0),20);
            Core.addWeighted(filterMat,-4.0,gaussian,5.0,0.0,ums);

            Imgproc.cvtColor(ums,ums,Imgproc.COLOR_RGB2HSV);

            Core.inRange(ums,new Scalar(0,0,221),new Scalar(180,30,255),ums);


            //public void detectMultiScale(Mat image, MatOfRect objects, double scaleFactor, int minNeighbors, int flags, Size minSize, Size maxSize)
//            Imgproc.cvtColor(ums,ums,Imgproc.COLOR_RGB2HSV);
            
//            Core.inRange(ums,new Scalar(156,43,46),new Scalar(180,255,255),ums);
//            Mat ums = new Mat();
//            Utils.bitmapToMat(copyOriginal,ums);
//
//
//            int height = ums.height();
//            int width = ums.width();
//            int sampleCount = width * height;
//            Mat sampleData = ums.reshape(4, sampleCount);
//            Mat data = new Mat();
//            sampleData.convertTo(data,CvType.CV_32F);
//            byte [][] colorTab ={
//                    {0,0, (byte) 255, (byte) 255},
//                    {0,(byte) 255,0, (byte) 255},
//                    {(byte) 255,0,0, (byte) 255},
//                    {(byte) 255,0, (byte) 255, (byte) 255}
//            };
//
//            int clusterCount = 4;
//            Mat labels = new Mat();
//            Mat centers = new Mat();
//            TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.COUNT, 10, 0.1);
//            Core.kmeans(data,clusterCount,labels,criteria,clusterCount, Core.KMEANS_PP_CENTERS,centers);
//
//            int index = 0;
//            Mat result = Mat.zeros(ums.size(),ums.type());
//            for (int i = 0; i < height; i++) {
//               for (int j = 0; j < width; j++) {
//                  index = i * width + j;
//                  int label = (int) labels.get(index, 0)[0];
//                  result.put(i,j,colorTab[label % 4]);
//               }
//            }






            Utils.matToBitmap(ums,copyOriginal);


            int width = copyOriginal.getWidth();
            int height = copyOriginal.getHeight();
            int [] filterPixels = new int[width * height];
            copyOriginal.getPixels(filterPixels,0,width,0,0,width,height);
            for (int i = 0; i < filterPixels.length; i++) {
               int pixels = filterPixels[i];
               if (Color.BLACK==pixels){
                  filterPixels[i] = 0x00000000;
               }
            }
            Bitmap basemapBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            basemapBitmap.setPixels(filterPixels,0,width, 0, 0, width, height);


            Bitmap resultBitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(basemapBitmap,0,0,null);


            filterInfoResult.setFilterBitmap(resultBitmap);
            filterInfoResult.setType(FilterType.FACE_ACNE);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         LogUtils.e(e.toString());
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }
}
