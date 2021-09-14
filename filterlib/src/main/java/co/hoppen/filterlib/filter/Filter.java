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


}
