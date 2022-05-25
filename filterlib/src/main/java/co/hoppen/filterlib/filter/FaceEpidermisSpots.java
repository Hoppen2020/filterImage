package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import androidx.recyclerview.widget.AsyncListUtil;

import com.blankj.utilcode.util.LogUtils;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * 表面斑 RGB light
 * Created by YangJianHui on 2022/3/7.
 */
public class FaceEpidermisSpots extends Filter implements FaceFilter{

    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){
                Bitmap cacheBitmap =
                        //getRgbSkin();
                        getFacePartImage();

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
                        Imgproc.drawContours(resultMat,list,i,new Scalar(255,0,0,255));
                    }
                }

                Utils.matToBitmap(resultMat,cacheBitmap);

                resultMat.release();
                frameMat.release();
                hierarchy.release();

                Bitmap resultBitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);
                Canvas canvas = new Canvas(resultBitmap);
                canvas.drawBitmap(cacheBitmap,0,0,null);
                if (!cacheBitmap.isRecycled())cacheBitmap.recycle();
                filterInfoResult.setFilterBitmap(resultBitmap);
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

    @Override
    public FacePart[] getFacePart() {
        return new FacePart[]{FacePart.FACE_FOREHEAD,FacePart.FACE_LEFT_RIGHT_AREA};
    }
}