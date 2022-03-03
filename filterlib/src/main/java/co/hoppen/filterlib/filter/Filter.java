package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import co.hoppen.filterlib.FilterInfoResult;

/**
 * Created by YangJianHui on 2021/9/10.
 */
public abstract class Filter {
    private Bitmap originalImage;
    private float resistance;

    Filter(){
    }

    public Filter setOriginalImage(Bitmap originalImage) {
        this.originalImage = originalImage;
        return this;
    }

    public Filter setResistance(float resistance) {
        this.resistance = resistance;
        return this;
    }

    protected Bitmap getOriginalImage() {
        return originalImage;
    }

    protected float getResistance() {
        return resistance;
    }

    protected boolean isEmptyBitmap(final Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    public abstract FilterInfoResult onFilter();


    public double rgb2Hsv(int rgb) {
        int r = Color.red(rgb);
        int g = Color.green(rgb);
        int b = Color.blue(rgb);
        double h = 0;
        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        if (max == min) {
            h = 0;
        } else {
            if (r == max)
                h = (double)(g - b) / (max - min);
            else if (g == max)
                h = 2 + (double)(b - r) / (max - min);
            else
                h = 4 + (double)(r - g) / (max - min);
        }
        h = h * 60;
        if (h < 0)
            h = h + 360;
        return h;
    }

    public Bitmap hairRemoval(){

        return null;
    }

    protected Bitmap toGray(boolean dark){
        if (originalImage==null)return null;
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
            int rgb = Color.rgb(gray, gray, gray);
            int color = (int) (rgb + (rgb-128) * (1.0f+0f) /255);
            filterPixels[i] = dark?color:rgb;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        bitmap.setPixels(filterPixels,0,width, 0, 0, width, height);
        return bitmap;
    }

    public int oppositeColorValue(int value){
        int opposite = 255 - value;
        if(opposite>64 && opposite<128)
            opposite-=64;
        else if(opposite>=128 && opposite<192)
            opposite+=64;
        return opposite;
    }

}
