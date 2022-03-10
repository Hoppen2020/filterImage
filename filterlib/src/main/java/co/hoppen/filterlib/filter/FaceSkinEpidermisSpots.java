package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/7.
 */
public class FaceSkinEpidermisSpots extends Filter{

    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){

                Bitmap bitmap = originalImage.copy(Bitmap.Config.ARGB_8888,true);

                //LogUtils.e(bitmap,originalImage);

                Mat grayMat = new Mat();
                Utils.bitmapToMat(bitmap,grayMat);
                Imgproc.cvtColor(grayMat,grayMat,Imgproc.COLOR_RGB2GRAY);

                Mat kernelX = new Mat(1,3,CvType.CV_16S);

                Mat result = new Mat();

                Imgproc.Sobel(grayMat,result,-1,0,1,9);

                Core.convertScaleAbs(result, result);

//                Core.bitwise_not(result,result);

                //kernelX.put(0,0,new short[]{-1,0,-1});
//                Mat resultKernelX = new Mat();
//                Imgproc.filter2D(grayMat, resultKernelX, CvType.CV_16S, kernelX);
//                Core.convertScaleAbs(resultKernelX, resultKernelX);
                Utils.matToBitmap(result,bitmap);

                int height = originalImage.getHeight();
                int width = originalImage.getWidth();

                int [] pixels = new int[width * height];
                int [] dstPixels = new int[width * height];

                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                originalImage.getPixels(dstPixels, 0, width, 0, 0, width, height);

                for (int i = 0; i < pixels.length; i++) {
                    int pixel = pixels[i];
                    int r = Color.red(pixel);
                    int g = Color.green(pixel);
                    int b = Color.blue(pixel);
                    if (Color.rgb(r,g,b)==Color.BLACK) dstPixels[i] = Color.rgb(205,183,158);
                }

                Bitmap resultBitmap = Bitmap.createBitmap(dstPixels, width, height, Bitmap.Config.ARGB_8888);

                filterInfoResult.setFilterBitmap(resultBitmap);
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
    public class ColorBlobDetector {
        // Lower and Upper bounds for range checking in HSV color space
        private Scalar mLowerBound = new Scalar(0);
        private Scalar mUpperBound = new Scalar(0);
        // Minimum contour area in percent for contours filtering
        private double mMinContourArea = 0.1;
        // Color radius for range checking in HSV color space
        private Scalar mColorRadius = new Scalar(25,50,50,0);
        private Mat mSpectrum = new Mat();
        private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

        // Cache
        Mat mPyrDownMat = new Mat();
        Mat mHsvMat = new Mat();
        Mat mMask = new Mat();
        Mat mDilatedMask = new Mat();
        Mat mHierarchy = new Mat();

        public void setColorRadius(Scalar radius) {
            mColorRadius = radius;
        }

        public void setHsvColor(Scalar hsvColor) {
            double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
            double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

            mLowerBound.val[0] = minH;
            mUpperBound.val[0] = maxH;

            mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
            mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

            mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
            mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

            mLowerBound.val[3] = 0;
            mUpperBound.val[3] = 255;

            Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

            for (int j = 0; j < maxH-minH; j++) {
                byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
                spectrumHsv.put(0, j, tmp);
            }

            Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
        }

        public Mat getSpectrum() {
            return mSpectrum;
        }

        public void setMinContourArea(double area) {
            mMinContourArea = area;
        }

        public void process(Mat rgbaImage) {
            Imgproc.pyrDown(rgbaImage, mPyrDownMat);
            Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

            Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

            Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
            Imgproc.dilate(mMask, mDilatedMask, new Mat());

            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

            Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            // Find max contour area
            double maxArea = 0;
            Iterator<MatOfPoint> each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint wrapper = each.next();
                double area = Imgproc.contourArea(wrapper);
                if (area > maxArea)
                    maxArea = area;
            }

            // Filter contours by area and resize to fit the original image size
            mContours.clear();
            each = contours.iterator();
            while (each.hasNext()) {
                MatOfPoint contour = each.next();
                if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                    Core.multiply(contour, new Scalar(4,4), contour);
                    mContours.add(contour);
                }
            }
        }

        public List<MatOfPoint> getContours() {
            return mContours;
        }
    }
}