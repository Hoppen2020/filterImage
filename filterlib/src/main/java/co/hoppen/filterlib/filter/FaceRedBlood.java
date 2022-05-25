package co.hoppen.filterlib.filter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.MSER;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/4/11.
 */
public class FaceRedBlood extends Filter implements FaceFilter{

   @Override
   public FilterInfoResult onFilter() {
      FilterInfoResult filterInfoResult = new FilterInfoResult();
      try {
         Bitmap originalImage = getOriginalImage();
         if (!isEmptyBitmap(originalImage)){
            Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

            Mat resultMat = new Mat();
            Utils.bitmapToMat(bitmap,resultMat);

            SimpleBlobDetector_Params params = new SimpleBlobDetector_Params();
            params.set_minThreshold(0f);
            params.set_maxThreshold(255f);
            params.set_filterByArea(true);
            params.set_minArea(5);
            params.set_filterByCircularity(true);
            params.set_minCircularity(0.7f);
            params.set_filterByConvexity(true);
            params.set_minConvexity(0.87f);
            params.set_filterByInertia(false);
            params.set_minInertiaRatio(0.5f);
            MatOfKeyPoint keyPoints = new MatOfKeyPoint();
            SimpleBlobDetector detector = SimpleBlobDetector.create(params);
            detector.detect(resultMat,keyPoints);


            LogUtils.e(keyPoints.toList().size());

            Mat dst = new Mat();
            Features2d.drawKeypoints(resultMat,keyPoints,dst, new Scalar(0,255,255),Features2d.DrawMatchesFlags_DRAW_RICH_KEYPOINTS);

            Utils.matToBitmap(dst,bitmap);


//            CLAHE clahe = Imgproc.createCLAHE(5,new Size(3,3));
//            Mat strengthenMat = new Mat();
//            Utils.bitmapToMat(bitmap,strengthenMat);
//            Imgproc.cvtColor(strengthenMat,strengthenMat,Imgproc.COLOR_RGBA2RGB);
//
//            List<Mat> list = new ArrayList<>();
//            Core.split(strengthenMat,list);
//            clahe.apply(list.get(0),list.get(0));
//            clahe.apply(list.get(1),list.get(1));
//            clahe.apply(list.get(2),list.get(2));
//            Core.merge(list,strengthenMat);
//            Utils.matToBitmap(strengthenMat,bitmap);

//            Mat resultMat = new Mat();
//            Utils.bitmapToMat(bitmap,resultMat);
//
//            Mat vChannel = getHsvV(bitmap);
//            Mat aChannel = getLabAChannel(bitmap);
//
//            List<Mat> strengthen = new ArrayList<>();
//            Mat zeros = Mat.zeros(vChannel.rows(), vChannel.cols(), CvType.CV_8UC1);
//            strengthen.add(zeros);
//            strengthen.add(aChannel);
//            strengthen.add(vChannel);
//
//            Mat result = new Mat();
//            Core.merge(strengthen,result);
//            Imgproc.cvtColor(result,result,Imgproc.COLOR_HSV2RGB);
//            Utils.matToBitmap(result,bitmap);
//
//            Bitmap mask = createMask(bitmap);
//
//            result.release();

            filterInfoResult.setFilterBitmap(bitmap);
            filterInfoResult.setType(FilterType.FACE_RED_BLOOD);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
         LogUtils.e(e.getMessage());
      }
      return filterInfoResult;
   }

   private Bitmap createMask(Bitmap bitmap) {
      int height = bitmap.getHeight();
      int width = bitmap.getWidth();
      int count = height * width;

      int [] maskPixels = new int[count];
      int [] returnPixels = new int[count];
      bitmap.getPixels(maskPixels,0,width,0,0,width,height);

      double [] lab = new double[3];
      for (int i = 0; i <maskPixels.length ; i++) {
         int originalPixel = maskPixels[i];
         int R = Color.red(originalPixel);
         int G = Color.green(originalPixel);
         int B = Color.blue(originalPixel);
         ColorUtils.RGBToLAB(R,G,B,lab);
         double gray = lab[1] - (G / 10) - (B /15) - 30;
         gray = (-0.00009685d * Math.pow(gray,3)) + (0.03784d * Math.pow(gray,2)) - (2.673d * gray + 48.12d);
         int mask =0;
         if (gray<=0) {
            mask = 0;
         }else if (gray>=255){
            mask = 255;
         }else {
            mask = (int) gray;
         }
         returnPixels[i] = Color.rgb(mask,mask,mask);
      }

      Bitmap returnBitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
      returnBitmap.setPixels(returnPixels,0,width, 0, 0, width, height);


//      Mat returnMat = new Mat();
//      Utils.bitmapToMat(returnBitmap,returnMat);
//
//      List<Mat> list = new ArrayList<>();
//      Core.split(returnMat,list);
//      Mat mat = list.get(0);
//
//
//
//      CLAHE clahe = Imgproc.createCLAHE(10,new Size(8,8));
//      clahe.apply(mat,mat);
//      Imgproc.equalizeHist(mat,mat);

//      Utils.matToBitmap(mat,returnBitmap);

//      Mat hsvMat = new Mat(mat.rows(),mat.cols(),CvType.CV_8UC3);
//      Imgproc.cvtColor(hsvMat,hsvMat,Imgproc.COLOR_RGB2HSV);
//
//      //通道数
//      int channels = mat.channels();
//      int channels2 = hsvMat.channels();
//      //宽度
//      int col = mat.cols();
//      //高度
//      int row = mat.rows();
//      byte[] p1 = new byte[channels];
//      byte[] p2 = new byte[channels2];
//      LogUtils.e(mat.toString(),hsvMat.toString());
//      for (int h = 0; h < row; h++) {
//         for (int w = 0; w <= col; w++) {
//            mat.get(h, w, p1);
//            int value = p1[0] & 0xff;
//            hsvMat.get(h, w, p2);
//            if (value<25){
//               p2[0] = (byte) 176;
//               p2[1] = (byte)30;
//               p2[2] = (byte) 241;
//            }else if (value>=25){
//               p2[0] = (byte) 176;
//               p2[1] = (byte) (180 * ((value -25) / (255 - 25)) + 30);
//               p2[2] = (byte) (241 - (200 *((value -25) / (255 - 25))));
//            }
//            hsvMat.put(h, w, p2);
//         }
//      }
//      Imgproc.cvtColor(hsvMat,hsvMat,Imgproc.COLOR_HSV2RGB);
//      Utils.matToBitmap(hsvMat,returnBitmap);

//      returnPixels = new int[count];
//      returnBitmap.getPixels(returnPixels,0,width,0,0,width,height);
//
//      for (int i = 0; i <returnPixels.length ; i++) {
//         int returnPixel = returnPixels[i];
//         int R = Color.red(returnPixel);
//         int G = Color.green(returnPixel);
//         int B = Color.blue(returnPixel);
//         double r = -0.00003566d * Math.pow(R,3) + 0.01467d * Math.pow(R,2) -0.4392d * R - 6.082d;
//         if (r<=0) {
//            R = 0;
//         } else if(r>=255) {
//            R = 255;
//         }else R = (int) r;
//         double g = -0.00003566d * Math.pow(G,3) + 0.01467d * Math.pow(G,2) -0.4392d * G - 6.082d;
//         if (g<=0) {
//            G = 0;
//         } else if(r>=255) {
//            G = 255;
//         }else G = (int) g;
//         double b = -0.00003566d * Math.pow(B,3) + 0.01467d * Math.pow(B,2) -0.4392d * B - 6.082d;
//         if (b<=0) {
//            B = 0;
//         } else if(r>=255) {
//            B = 255;
//         }else B = (int) b;
//         returnPixels[i] = Color.rgb(R,G,B);
//      }
//      returnBitmap.setPixels(returnPixels,0,width, 0, 0, width, height);


      return returnBitmap;
   }

   private Mat getHsvV(Bitmap bitmap) {
      Mat hsvMat = new Mat();
      Utils.bitmapToMat(bitmap,hsvMat);
      Imgproc.cvtColor(hsvMat,hsvMat,Imgproc.COLOR_RGB2HSV);
      List<Mat> hsvList = new ArrayList<>();
      Core.split(hsvMat,hsvList);
      hsvMat.release();
      return hsvList.get(2);
   }

   private Mat getLabAChannel(Bitmap bitmap) {
      Mat labMat = new Mat();
      Utils.bitmapToMat(bitmap,labMat);
      Imgproc.cvtColor(labMat,labMat,Imgproc.COLOR_RGB2Lab);
      List<Mat> labList = new ArrayList<>();
      Core.split(labMat,labList);
      labMat.release();
      Mat aChannel = labList.get(1);
      //转32位浮点类型
      aChannel.convertTo(aChannel, CvType.CV_32F,1.0/255);
      //通道数
      int channels = aChannel.channels();
      //宽度
      int col = aChannel.cols();
      //高度
      int row = aChannel.rows();
      float[] p = new float[channels];
      float min = 0.50196f;//0.50196f
      float max = 0.6549f;//0.6549f
      for (int h = 0; h < row; h++) {
         for (int w = 0; w <= col; w++) {
            aChannel.get(h, w, p);
            float value = p[0];
            if (value<min){
               value=0;
            }else if (value>max){
               value = 1;
            }else {
               value = (value - min) / (max-min);
            }
            p[0] = value;
            aChannel.put(h, w, p);
         }
      }
      aChannel.convertTo(aChannel,CvType.CV_8UC3,255);
      return aChannel;
   }

   @Override
   public FacePart[] getFacePart() {
      return new FacePart[]{FacePart.FACE_SKIN};
   }
}
