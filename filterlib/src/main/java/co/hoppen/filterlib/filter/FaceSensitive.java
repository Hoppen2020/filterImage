package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import org.opencv.photo.Photo;

import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/12.
 */
public class FaceSensitive extends Filter implements FaceFilter{


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

            Mat skinMat = new Mat();
            Utils.bitmapToMat(bitmap,skinMat);

            Imgproc.cvtColor(skinMat,skinMat,Imgproc.COLOR_RGBA2RGB);

            Mat hsv = new Mat();
            Imgproc.cvtColor(skinMat,hsv,Imgproc.COLOR_RGB2HSV);

            List<Mat> list = new ArrayList<>();
            Core.split(hsv,list);

//            Mat mat = list.get(1);
//
//            Core.inRange(mat,new Scalar(30,30,30),new Scalar(200,200,200),mat);

            Core.bitwise_not(skinMat,skinMat);

            Imgproc.cvtColor(skinMat,skinMat,Imgproc.COLOR_RGB2HSV);
            Mat redLightMask = new Mat();
            Mat redDarkMask = new Mat();
            Core.inRange(skinMat,new Scalar(156,43,46),new Scalar(180,255,255),redLightMask);
            Core.inRange(skinMat,new Scalar(0,43,46),new Scalar(10,255,255),redDarkMask);

            Mat addMask = new Mat();
            Core.add(redLightMask,redDarkMask,addMask);
            Core.bitwise_not(addMask,addMask);

            Mat grayMat = new Mat();
            Imgproc.cvtColor(oriMat,grayMat,Imgproc.COLOR_RGB2GRAY);

            Imgproc.threshold(grayMat,grayMat,0,255,Imgproc.THRESH_OTSU|Imgproc.THRESH_BINARY);

            Mat mixMask = new Mat();
            Core.bitwise_and(addMask,grayMat,mixMask,addMask);

            Mat faceMat = new Mat();

            Core.bitwise_and(oriMat,oriMat,faceMat,mixMask);

//            Mat red = new Mat(oriMat.rows(),oriMat.cols(),CvType.CV_8UC3);
//            red.setTo(new Scalar(163,46,63));//163,46,63

//            Mat dst = new Mat();
//
//            Core.addWeighted(faceMat,1.0,red,0.7,0.,dst);

            Imgproc.cvtColor(faceMat,faceMat,Imgproc.COLOR_RGB2HSV);

            List<Mat> hsvList = new ArrayList<>();
            Core.split(faceMat,hsvList);

            Mat vChannelMat = new Mat();
            Core.add(hsvList.get(1),hsvList.get(1),vChannelMat);
            Core.add(vChannelMat,new Scalar(5,5,5),vChannelMat);
            hsvList.set(1,vChannelMat);


            Core.merge(hsvList,vChannelMat);

//            Core.inRange(vChannelMat,new Scalar(156,43,46),new Scalar(180,255,255),redLightMask);
//            Core.inRange(vChannelMat,new Scalar(0,43,46),new Scalar(10,255,255),redDarkMask);
//
//            Core.add(redLightMask,redDarkMask,addMask);
//
//            Core.bitwise_and(vChannelMat,vChannelMat,faceMat,addMask);
            Imgproc.cvtColor(vChannelMat,vChannelMat,Imgproc.COLOR_HSV2RGB);

            Utils.matToBitmap(vChannelMat,bitmap);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int [] filterPixels = new int[width * height];
//            Bitmap filterBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            int [] basemap = new int[width * height];
            bitmap.getPixels(filterPixels,0,width,0,0,width,height);
            for (int i = 0; i < filterPixels.length; i++) {
               int pixels = filterPixels[i];
               if (Color.BLACK==pixels){
                  basemap[i] = 0x00000000;
               }else {
                  basemap[i] = filterPixels[i];
               }
            }
            Bitmap basemapBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            basemapBitmap.setPixels(basemap,0,width, 0, 0, width, height);

            Bitmap resultBitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.drawBitmap(basemapBitmap,0,0,null);
            if (!bitmap.isRecycled())bitmap.recycle();
            if (!basemapBitmap.isRecycled())basemapBitmap.recycle();

            filterInfoResult.setFilterBitmap(resultBitmap);
            filterInfoResult.setType(FilterType.FACE_SENSITIVE);
            filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
         }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }catch (Exception e){
         filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
      }
      return filterInfoResult;
   }

   @Override
   public FacePart[] getFacePart() {
      return new FacePart[]{FacePart.FACE_SKIN};
   }
}
