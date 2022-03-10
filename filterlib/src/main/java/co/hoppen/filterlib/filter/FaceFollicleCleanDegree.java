package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/8.
 */
public class FaceFollicleCleanDegree extends Filter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){

            Mat mat = new Mat();
            Utils.bitmapToMat(originalImage,mat);

            Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGBA2RGB);

            List<Mat> color = new ArrayList<>();
            Core.split(mat,color);

//                LogUtils.e(color.size());

            Mat mat1 = color.get(0);

            Imgproc.threshold(mat1,mat1,95,255,Imgproc.THRESH_BINARY);

            Mat strElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                    new Size(5, 5), new Point(-1, -1));

            Imgproc.dilate(mat1,mat1,strElement);


            Imgproc.medianBlur(mat1,mat1,3);

            Mat circles = new Mat();//存储线的容器
//                Imgproc.HoughCircles(mat1,circles,Imgproc.CV_HOUGH_GRADIENT,2,5,10,15,1,30);
//                for(int i = 0;i<circles.cols();i++){
//                    float[] circle = new float[3];
//                    circles.get(0,i,circle);//将圆对应的坐标存到circle数组中
//                    Imgproc.circle(mat1,new Point(circle[0],circle[1]), (int) circle[2],new Scalar(255,0,0),2,Imgproc.LINE_AA);//画边缘
//                }

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            Bitmap filterBitmap = Bitmap.createBitmap(originalImage.getWidth(),originalImage.getHeight(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat1,filterBitmap);

            int [] originalPixels = new int[width * height];
            originalImage.getPixels(originalPixels,0,width,0,0,width,height);

            int [] pixels = new int[width * height];
            filterBitmap.getPixels(pixels,0,width,0,0,width,height);
            for (int i = 0; i < pixels.length; i++) {
               if (pixels[i]== Color.WHITE){
                  originalPixels[i] =Color.RED;
               }
            }

            Bitmap newBitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
            newBitmap.setPixels(originalPixels,0,width, 0, 0, width, height);


            filterInfoResult.setFilterBitmap(newBitmap);
            filterInfoResult.setType(FilterType.FOLLICLE_CLEAN_DEGREE);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else{
            filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
         }
      } catch (Exception e) {
         LogUtils.e(e.toString());
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

}
