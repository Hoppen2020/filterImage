package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.recyclerview.widget.AsyncListUtil;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
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
 * 表面斑 RGB light
 * Created by YangJianHui on 2022/3/7.
 */
public class FaceEpidermisSpots extends Filter{

    private Size maxSize;

    private Size minSize;

    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){
                Bitmap cacheBitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

                Mat resultMat = new Mat();
                Utils.bitmapToMat(cacheBitmap,resultMat);

                Mat frameMat = new Mat();
                Utils.bitmapToMat(cacheBitmap,frameMat);

                Imgproc.cvtColor(frameMat,frameMat,Imgproc.COLOR_RGBA2GRAY);

                Imgproc.equalizeHist(frameMat,frameMat);

                Imgproc.GaussianBlur(frameMat,frameMat,new Size(3,3),0);

                Imgproc.adaptiveThreshold(frameMat,frameMat,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV,99,10);

                Imgproc.morphologyEx(frameMat,frameMat,Imgproc.MORPH_OPEN,Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3)));

                List<MatOfPoint> list = new ArrayList<>();

                Mat hierarchy = new Mat();

                Imgproc.findContours(frameMat,list,hierarchy,Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_SIMPLE);

                LogUtils.e(list.size());

                for (int i = 0; i < list.size(); i++) {
                    MatOfPoint point = list.get(i);
                    if (point.size().area()>5 && point.size().area()<=200){
                        Imgproc.drawContours(resultMat,list,i,new Scalar(255,0,0));
                    }
                }

                Utils.matToBitmap(resultMat,cacheBitmap);


                int width = originalImage.getWidth();
                int height = originalImage.getHeight();
                int count = width * height;
                int [] originalPixels = new int[count];
                int [] filterPixels = new int[count];
                int [] result = new int[count];
                originalImage.getPixels(originalPixels,0,width,0,0,width,height);
                cacheBitmap.getPixels(filterPixels,0,width,0,0,width,height);

                for (int i = 0; i < originalPixels.length; i++) {
                    int originalPixel = originalPixels[i];
                    if (Color.alpha(originalPixel)==0){
                        result[i] = 0x00000000;
                        break;
                    }else {
                        result[i] = filterPixels[i];
                    }
                }
                Bitmap createBitmap = Bitmap.createBitmap(result,width,height, Bitmap.Config.ARGB_8888);

                filterInfoResult.setFilterBitmap(createBitmap);
                filterInfoResult.setType(FilterType.FACE_EPIDERMIS_SPOTS);//FaceEpidermisSpots
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