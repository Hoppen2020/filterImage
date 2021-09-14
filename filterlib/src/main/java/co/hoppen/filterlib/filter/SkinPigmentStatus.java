package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2021/9/10.
 */
public class SkinPigmentStatus extends Filter {

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
                int totalGray = 0;
                double totalHsv = 0;
                double avgHsv = 0;
                int majorColor = Color.BLACK;
                int countRgb = 0;
                for (int i = 0; i <originalPixels.length ; i++) {
                    int originalPixel = originalPixels[i];
                    int R = Color.red(originalPixel);
                    int G = Color.green(originalPixel);
                    int B = Color.blue(originalPixel);
                    int gray = (int) (R * 0.3 + G * 0.59 + B * 0.11);
                    totalGray+=gray;
                    filterPixels[i] = gray;

                    double hsv = rgb2Hsv(originalPixel);
                    totalHsv +=hsv;

                }
                avgHsv = totalHsv / count;
                List<Integer> thresholdList = new ArrayList<Integer>();
                for (int i = 0; i <originalPixels.length ; i++) {
                    int originalPixel = originalPixels[i];
                    double hsv = rgb2Hsv(originalPixel);
                    if (Math.abs(hsv - avgHsv) > 30) {//threshold = 30
                        thresholdList.add(originalPixel);
                    }
                }
                if (thresholdList.size()!=0){
                    int sumR = 0,sumG = 0, sumB = 0;
                    for (int rgb : thresholdList){
                        sumR += Color.red(rgb);
                        sumG += Color.green(rgb);
                        sumB += Color.blue(rgb);
                    }
                    majorColor = Color.rgb(sumR / thresholdList.size(), sumG / thresholdList.size(), sumB / thresholdList.size());
                }

                int verGray=(int) (totalHsv/filterPixels.length);
                for (int i = 0; i <originalPixels.length ; i++) {
                    int originalPixel = originalPixels[i];
                    filterPixels[i] = originalPixel;
                    double light = verGray / 255d;
                    double modulusDou = 255.0 / 90d;
                    double modulusGenRG = (Math.sqrt(21.49 * 21.49 - 4 * 0.4371 * verGray) + 21.49) / (2 * 0.4371);
                    double modulusGenGR = modulusGenRG - 20;
                    int rJianG = (Math.abs(Color.red(majorColor) - Color.red(originalPixel)) - (Math.abs(Color.green(majorColor) - Color.green(originalPixel))));
                    int rJianB = (Math.abs(Color.red(majorColor) - Color.red(originalPixel)) - (Math.abs(Color.blue(majorColor) - Color.blue(originalPixel))));

                    if (verGray> 160){
                        double rChuG = 1;
                        if (Color.blue(originalPixel) > 0)
                            rChuG = (double) Color.red(originalPixel) / (double) Color.blue(originalPixel);
                        if (rChuG > 1.121 || Color.blue(originalPixel) == 0) {
                            int rColor = Color.red(originalPixel) * 3 <= 255 ? Color.red(originalPixel) * 3 : 255;
                            int gColor = Color.green(originalPixel) * 2 <= 255 ? Color.green(originalPixel) * 2 : 255;
                            filterPixels[i] = Color.rgb(rColor, gColor, Color.blue(originalPixel));
                            countRgb++;
                        }
                    }else {
                        if ((Color.red(originalPixel) - Color.blue(originalPixel) > 0 || (Color.green(originalPixel) - Color.blue(originalPixel) > 0)) && ((Color.green(originalPixel) - Color.red(originalPixel)) < modulusGenGR && (Color.red(originalPixel) - Color.green(originalPixel)) < modulusGenRG))// || float_R / float_B > 1.2
                        {
                            if (((Math.abs(Color.red(majorColor) - Color.red(originalPixel)) > 20 * light * modulusDou) || (Math.abs(Color.green(majorColor) - Color.green(originalPixel)) > 15 * light * modulusDou) && rJianG < 10) ||
                                    ((Math.abs(Color.red(majorColor) - Color.red(originalPixel)) > 20 * light * modulusDou) || (Math.abs(Color.blue(majorColor) - Color.blue(originalPixel)) > 15 * light * modulusDou) && rJianB < 10)) {
                                int RColor = Color.red(originalPixel) * 2 <= 255 ? Color.red(originalPixel) * 2 : 255;
                                int GColor = Color.green(originalPixel) * 2 <= 255 ? Color.green(originalPixel) * 2 : 255;
                                filterPixels[i] = Color.rgb(RColor, GColor, Color.blue(originalPixel));
                                countRgb++;
                            }
                        }
                    }
                    if (Color.red(originalPixel) <= 130 && Color.blue(originalPixel) <= 40) {
                        int RColor = Color.red(originalPixel) * 3 <= 255 ? Color.red(originalPixel) * 3 : 255;
                        int GColor = Color.green(originalPixel) * 2 <= 255 ? Color.green(originalPixel) * 2 : 255;
                        filterPixels[i] = Color.rgb(RColor, GColor, Color.blue(originalPixel));
                        countRgb++;
                    }
                }
                int score = 0;
                if (countRgb <= (float)640 * 480 *1.0/ 20.0) {
                    score = (int) (70 + 20 * (1.0 - (countRgb * 20 /1.0) / (count)));//70-80 , 640*480
                } else if (countRgb > count *1.0 / 20.0 && countRgb <= count * 4.0d /20.0) {
                    score = (int) (45 + 25 * (1.0 - (countRgb - count *1.0/ 20.0) * 20.0/3.0  /(count) ));
                } else if (countRgb > count *4.0/ 20d && countRgb <= count * 6.0d /20.0) {
                    score = (int) (25 + 20 * (1.0 - (countRgb - count * 5.0 / 20.0) * 10.0 / (count)));
                } else if (countRgb > count *6.0/ 20d && countRgb <= count *10.0/20.0) {
                    score = (int) (20 + 5 * (1.0 - (countRgb - count * 6.0 / 20.0) * 5.0 /(count) ));
                }else{
                    score = 20 + new Random().nextInt(3);
                }
                if (score < 25) {
                    Random random=new Random();
                    score = 25+random.nextInt(5);
                }
                if (score > 85) {
                    Random random=new Random();
                    score = 80+random.nextInt(5);
                }

                Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
                bitmap.setPixels(filterPixels,0,width, 0, 0, width, height);
                filterInfoResult.setResistance(getResistance());
                filterInfoResult.setScore(score);
                filterInfoResult.setRatio((countRgb * 100 / count));
                filterInfoResult.setFilterBitmap(bitmap);
                filterInfoResult.setType(FilterType.SKIN_PIGMENT_STATUS);
                filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
            }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }catch (Exception e){
            filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }
        return filterInfoResult;
    }
}
