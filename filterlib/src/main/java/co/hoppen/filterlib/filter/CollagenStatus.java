package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;

import java.util.Random;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2021/9/11.
 */
public class CollagenStatus extends Filter{
    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                int dst[] = new int[width * height];
                originalImage.getPixels(dst, 0, width, 0, 0, width, height);
                int pos;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        pos = y * width + x;
                        dst[pos] = 0XFF000000 | (~dst[pos] & 0x00ffffff);
                    }
                }
                bitmap.setPixels(dst, 0, width, 0, 0, width, height);
                Random random = new Random();
                int score = random.nextInt(23) + 49;
                if (score < 49) {
                    score = 49 + random.nextInt(4);
                }
                if (score > 72) {
                    score = 72 - random.nextInt(4);
                }

                filterInfoResult.setResistance(getResistance());
                filterInfoResult.setScore(score);
                filterInfoResult.setRatio(0);
                filterInfoResult.setFilterBitmap(bitmap);
                filterInfoResult.setType(FilterType.COLLAGEN_STATUS);
                filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
            }else filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }catch (Exception e){
            filterInfoResult.setStatus(FilterInfoResult.Status.FAILURE);
        }
        return filterInfoResult;
    }
}
