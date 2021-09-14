package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.blankj.utilcode.util.LogUtils;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2021/9/11.
 */
public class FollicleCleanDegree extends Filter{
    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();
                int count = width * height;
                int [] originalPixels = new int[count];
                int [] filterPixels = new int[count];
                originalImage.getPixels(originalPixels,0,width,0,0,width,height);
                int totalReds = 0;
                double totalHsv = 0;
                int countRgb = 0;
                for (int i = 0; i <originalPixels.length ; i++) {
                    int originalPixel = originalPixels[i];
                    int r = Color.red(originalPixel);
                    totalReds+=r;
                    filterPixels[i] = r;
                }
                int verGray=(int) (totalReds/filterPixels.length);

                for (int i = 0; i <originalPixels.length ; i++) {
                    int originalPixel = originalPixels[i];
                    filterPixels[i] = originalPixel;

                    int r = Color.red(originalPixel);
                    int g = Color.green(originalPixel);
                    int b = Color.blue(originalPixel);

                    double ratioRed = 1.0;

                    if (r >= 40 && r <= 80)
                        ratioRed = 3.0 - (r - 40) / 20;
                    else if (r < 40) {
                        ratioRed = 2.5 + (40 - r) / 40;
                    } else {
                        ratioRed = 1.4;
                    }

                    float float_R = (float) (r) / 765;
                    float float_G = (float) (g) / 765;
                    float float_B = (float) (b) / 765;

                    if ((float_R / float_G > ratioRed || (float_R / float_B > ratioRed)) && r > verGray * 1.1) {
                        if (!((float_B > 150 || float_G > 150) && Math.abs(float_B - float_G) <= 10)) {
                            filterPixels[i] = Color.rgb(255, (int) float_G, (int) float_B);
                            countRgb++;
                        }
                    }
                }
                int score = 0;
                if (countRgb < (float)count / 50d) {
                    score = (int) (65 + 25 * (1.0 - (countRgb * 50) / (count)));
                } else if (countRgb >= count / 50d && countRgb < count /25.0) {
                    score = (int) (50 + 15 * (1.0 - (countRgb - 640*480 / 50) * 50 / (count)));
                } else if (countRgb >= count / 25d && countRgb < count *2.0 /20.0) {
                    score = (int) (35 + 15 * (1.0 - (countRgb - 640*480 / 25.0) * 100.0 / 6.0 / (count)));
                } else if (countRgb >= count *2.0d /20d){
                    score = (int) (20 + 15 * (1.0 - (countRgb - 640*480 * 2.0 / 20.0) * 20.0 / 18.0 / (count)));
                }

                Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
                bitmap.setPixels(filterPixels,0,width, 0, 0, width, height);
                filterInfoResult.setResistance(getResistance());
                filterInfoResult.setScore(score);
                filterInfoResult.setRatio((countRgb * 100 / count));
                filterInfoResult.setFilterBitmap(bitmap);
                filterInfoResult.setType(FilterType.FOLLICLE_CLEAN_DEGREE);
                filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
            }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }catch (Exception e){
            LogUtils.e(e.getMessage());
            filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }
        return filterInfoResult;
    }
}
