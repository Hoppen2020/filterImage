package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2021/9/14.
 */
public class TestFilter extends Filter{
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

                for (int i = 0; i <originalPixels.length ; i++) {
                    int originalPixel = originalPixels[i];
                    int R = Color.red(originalPixel);
                    int G = Color.green(originalPixel);
                    int B = Color.blue(originalPixel);
                    int gray = (int) (R * 0.3 + G * 0.59 + B * 0.11);

                    filterPixels[i] = Color.rgb(gray,gray,gray);
                }

                Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
                bitmap.setPixels(filterPixels,0,width, 0, 0, width, height);
                filterInfoResult.setResistance(getResistance());
                filterInfoResult.setScore(0);
                filterInfoResult.setRatio(0);
                filterInfoResult.setFilterBitmap(bitmap);
                filterInfoResult.setType(FilterType.SKIN_RED_BLOOD_STATUS);
                filterInfoResult.setStatus(FilterInfoResult.Status.SUCCESS);
            }
        }catch (Exception e){

        }


        return filterInfoResult;
    }
}
