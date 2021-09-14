package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2021/9/11.
 */
public class SkinRedBloodStatus extends Filter {
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
                double totalGray = 0;
                int countRgb = 0;
                for (int i = 0; i <originalPixels.length ; i++) {
                    int originalPixel = originalPixels[i];
                    int R = Color.red(originalPixel);
                    int G = Color.green(originalPixel);
                    int B = Color.blue(originalPixel);
                    int gray = (int) (R * 0.3 + G * 0.59 + B * 0.11);
                    filterPixels[i] = gray;
                    totalGray += gray;
                }

                int verGray=(int) (totalGray/filterPixels.length);
                for (int i = 0; i <originalPixels.length ; i++) {
                    int originalPixel = originalPixels[i];
                    filterPixels[i] = originalPixel;


                    int r = Color.red(originalPixel);
                    int g = Color.green(originalPixel);
                    int b = Color.blue(originalPixel);

                    float sorcePre = 170f / (float)verGray;
                    verGray = 170;

                    float float_R = (float)((Color.red(originalPixel) * sorcePre) > 255 ? 255 : (float)((Color.red(originalPixel) * sorcePre)));
                    float float_G = (float)((Color.green(originalPixel) * sorcePre) > 255 ? 255 : (float)((Color.green(originalPixel) * sorcePre)));
                    float float_B = (float)((Color.blue(originalPixel) * sorcePre) > 255 ? 255 : (float)((Color.blue(originalPixel) * sorcePre)));

                    double light = (float)255 / verGray;
                    double criticalValueRG = (Math.sqrt(69.625 * 69.625 + 4 * 11.375 * verGray) + 69.625) / (2 * 11.375);
                    double criticalValueRB = criticalValueRG+1.815;

                    if (float_R / float_G > ((verGray * light / 30 / criticalValueRG)) && (float_R / float_B > (verGray * light / 30 / criticalValueRB))) {
                        if (float_G / float_B < 1.0 || float_R / float_B < 1.0) {
                            countRgb++;
                            filterPixels[i] = Color.rgb(r * 2 > 255 ? 255 : r * 2, 255 - g, 255 - b);
                        }
                    }
                    if (float_R / float_B >= 2) {
                        countRgb++;
                        //bp.SetPixel(i, j, Color.FromArgb(Convert.ToInt32(float_R * 2) > 255 ? 255 : Convert.ToInt32(float_R * 2), (255 - Convert.ToInt32(float_G)), (255 - Convert.ToInt32(float_B))));
                        filterPixels[i] = Color.rgb(r*2>255?255:r *2 , 255-g, 255-b);
                    }
                    verGray=(int) (totalGray/filterPixels.length);
                }
                int score = 0;
                if (countRgb <= (float)count / 25.0) {
                    score = (int) (65 + 25 * (1.0 - (countRgb * 25.0) / (count)));
                } else if (countRgb > count / 25.0 && countRgb <= count * 3.0/20) {
                   score  = (int) (50 + 15 * (1.0 - ((countRgb - (count / 25.0)) * 100.0 /11.0/ (count))));
                } else if (countRgb > count *3.0/ 20.0 && countRgb <= count *7.0/ 20.0){
                    score  = (int) (35 + 15 * (1.0 - ((countRgb - (count *3.0/20.0)) * 5.0 / (count))));
                } else if (countRgb > count * 7.0/20.0&& countRgb <=(count)/1.0){
                    score = (int) (20 + 15 * (1.0 - ((countRgb - (count) * 0.7/20.0) * 20.0 / 13.0 / (count))));
                }

                Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
                bitmap.setPixels(filterPixels,0,width, 0, 0, width, height);
                filterInfoResult.setResistance(getResistance());
                filterInfoResult.setScore(score);
                filterInfoResult.setRatio((countRgb * 100 / count));
                filterInfoResult.setFilterBitmap(bitmap);
                filterInfoResult.setType(FilterType.SKIN_RED_BLOOD_STATUS);
                filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
            }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }catch (Exception e){
            filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }
        return filterInfoResult;
    }
}
