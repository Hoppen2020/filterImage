package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/7.
 */
public class FaceWrinkle4 extends Filter implements FaceFilter{

    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){
                Bitmap cacheBitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

                Mat oriMat = new Mat();
                Utils.bitmapToMat(cacheBitmap,oriMat);
                Mat grayMat = new Mat();
                Imgproc.cvtColor(oriMat,grayMat,Imgproc.COLOR_RGBA2GRAY);

                Mat result = new Mat();

                Mat gaborKernel = Imgproc.getGaborKernel(new Size(5, 5), 8, 0 ,5, 0.5, 0, CvType.CV_32F);

                Imgproc.filter2D(grayMat,result,-1,gaborKernel,new Point(-1,-1),0,Core.BORDER_CONSTANT);

                Utils.matToBitmap(result,cacheBitmap);

                filterInfoResult.setFilterBitmap(cacheBitmap);
                filterInfoResult.setType(FilterType.FACE_WRINKLE);
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
        return new FacePart[]{FacePart.FACE_SKIN};
    }
}