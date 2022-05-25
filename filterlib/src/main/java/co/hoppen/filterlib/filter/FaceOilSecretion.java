package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;
import co.hoppen.filterlib.utils.CutoutUtils;

/**
 * Created by YangJianHui on 2021/9/10.
 */
public class FaceOilSecretion extends Filter implements FaceFilter {

    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){

                Bitmap bitmap = getFacePartImage().copy(Bitmap.Config.ARGB_8888,true);
                Mat yuvMat = new Mat();
                Utils.bitmapToMat(bitmap,yuvMat);
                Imgproc.cvtColor(yuvMat,yuvMat,Imgproc.COLOR_RGB2GRAY);
                Mat detect = new Mat();
                List<Mat> channels = new ArrayList<>();
                Core.split(yuvMat,channels);
                Mat outputMark = channels.get(0);
                Imgproc.threshold(outputMark,outputMark,0,255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);
                yuvMat.copyTo(detect,outputMark);
                Utils.matToBitmap(detect,bitmap);

                yuvMat.release();
                detect.release();
                outputMark.release();

                int width = originalImage.getWidth();
                int height = originalImage.getHeight();

                int [] pixels = new int[width * height];
                int [] dst = new int[width * height];
                int [] original = new int[width * height];

                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                originalImage.getPixels(original, 0, width, 0, 0, width, height);

                int totalGray = 0;
                int count = 0;

                for (int i = 0; i < pixels.length; i++) {
                    int r = Color.red(pixels[i]);
                    int g = Color.green(pixels[i]);
                    int b = Color.blue(pixels[i]);
                    int color = Color.rgb(r,g,b);
                    if (color!=Color.BLACK){
                        int gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                        totalGray +=gray;
                        count ++;
                    }
                }

                int avgGray = (int) (totalGray/count + (totalGray/count * 0.17f));

                for (int i = 0; i < pixels.length; i++) {
                    int r = Color.red(pixels[i]);
                    int g = Color.green(pixels[i]);
                    int b = Color.blue(pixels[i]);
                    int color = Color.rgb(r,g,b);
                    if (color==Color.BLACK) {
                        dst[i] = original[i];
                        continue;
                    }else {
                        int gray = (int) (r * 0.3 + g * 0.59 + b * 0.11);
                        if (gray>avgGray){
                            r = Color.red(original[i]);
                            g = Color.green(original[i]);
                            b = Color.blue(original[i]);
                            float [] hsl = new float[3];
                            ColorUtils.RGBToHSL(r,g,b,hsl);
                            float newL = hsl[2] + 0.1f;
                            if (newL>=1)newL = 1;
                            hsl[2] = newL;
                            dst[i] = ColorUtils.HSLToColor(hsl);
                        }else {
                            dst[i] = original[i];
                        }
                    }
                }
                if (!bitmap.isRecycled())bitmap.recycle();
                filterInfoResult.setFilterBitmap(Bitmap.createBitmap(dst,width,height, Bitmap.Config.ARGB_8888));
                filterInfoResult.setType(FilterType.FACE_OIL_SECRETION);
                filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
            }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }catch (Exception e){
            filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }
        return filterInfoResult;
    }

    @Override
    public FacePart[] getFacePart() {
        return new FacePart[]{FacePart.FACE_T,FacePart.FACE_LEFT_RIGHT_AREA};
    }

}
