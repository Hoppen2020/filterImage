package co.hoppen.filterlib;

import android.graphics.Bitmap;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by YangJianHui on 2021/9/10.
 */
public class FilterInfoResult {
    private FilterType type;

    private Bitmap filterBitmap;

    private int score;

    private float resistance;

    private double ratio;

    private float depth;

    private Status status = Status.NOT_CONVERTED;

    public Bitmap getFilterBitmap() {
        return filterBitmap;
    }

    public void setFilterBitmap(Bitmap filterBitmap) {
        this.filterBitmap = filterBitmap;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getResistance() {
        return resistance;
    }

    public void setResistance(float resistance) {
        this.resistance = resistance;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public String getRatioString(){
        BigDecimal bigDecimal = new BigDecimal(ratio).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue() + "%";
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "FilterInfoResult{" +
                "type=" + type +
                ", filterBitmap=" + filterBitmap +
                ", score=" + score +
                ", resistance=" + resistance +
                ", ratio=" + ratio +
                ", ratioString=" + getRatioString() +
                ", status=" + status +
                '}';
    }

    public enum Status{
        NOT_CONVERTED,
        SUCCESS,
        FAILURE
    }

}
