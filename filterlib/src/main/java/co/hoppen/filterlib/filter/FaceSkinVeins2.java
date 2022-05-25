package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;

import com.blankj.utilcode.util.ArrayUtils;
import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import co.hoppen.filterlib.FacePart;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/7.
 */
public class FaceSkinVeins2 extends Filter implements FaceFilter{

    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){
                Bitmap operateBitmap = getFacePartImage();
//                        originalImage.copy(originalImage.getConfig(),true);

                int width = operateBitmap.getWidth();
                int height = originalImage.getHeight();
                int count = width * height;

                int widthCount = 4;
                int heightCount = 6;
                int pieceWidth = width / widthCount;
                int pieceHeight = height / heightCount;
                List<PieceInfo> allPiece = new ArrayList<>(widthCount * heightCount);
                List<Point> allPoint = new ArrayList<>(allPiece.size());
                Bitmap returnBitmap = null;
                for (int i = 0; i < heightCount; i++) {
                    for (int j = 0; j < widthCount; j++) {
                        int index = j + i * widthCount;
                        int [] piecePixels = new int[pieceWidth * pieceHeight];
                        int x = j * pieceWidth;
                        int y = i * pieceHeight;
                        operateBitmap.getPixels(piecePixels,0,pieceWidth,x,y,pieceWidth,pieceHeight);
                        PieceInfo pieceInfo = new PieceInfo();
                        int totalN = 0;
                        for (int k = 0; k < piecePixels.length; k++) {
                            int pixel = piecePixels[k];
                            int r = Color.red(pixel);
                            int g = Color.green(pixel);
                            int b = Color.blue(pixel);
                            int gray = (r + g + b) / 3;
                            piecePixels[k] = pixel;
                            totalN +=gray;
                        }

                        allPoint.add(new Point(x,y));
                        pieceInfo.x = x;
                        pieceInfo.y = y;
                        pieceInfo.piece = piecePixels;
                        pieceInfo.avg = totalN / piecePixels.length;
                        pieceInfo.createMinus();
                        allPiece.add(pieceInfo);
                    }
                }
                Collections.sort(allPiece, new Comparator<PieceInfo>() {
                    @Override
                    public int compare(PieceInfo o1, PieceInfo o2) {
                        int a = o1.avgMinus;
                        int b = o2.avgMinus;
                        if (a>b){
                            return 1;
                        }else if (a==b){
                            return 0;
                        }else {
                            return -1;
                        }
                    }
                });
//                returnBitmap = Bitmap.createBitmap(allPiece.get(15).getPiece(),pieceWidth,pieceHeight, Bitmap.Config.ARGB_8888);

                int sumTemp = 0;
                int remainCount = 0;
                for (int i = 6; i < allPiece.size(); i++) {
                    sumTemp += allPiece.get(i).avgMinus;
                    remainCount++;
                }
                int coarseness = sumTemp / remainCount;
                LogUtils.e(coarseness);

                returnBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(returnBitmap);
                for (int i = 0; i < allPiece.size(); i++) {
                    PieceInfo pieceInfo = allPiece.get(i);
                    Point point = allPoint.get(i);
                    LogUtils.e(pieceInfo.avgMinus);
                    Bitmap bitmap = Bitmap.createBitmap(pieceInfo.piece, pieceWidth, pieceHeight, Bitmap.Config.ARGB_8888);
                    //pieceInfo.x pieceInfo.y
                    canvas.drawBitmap(bitmap,pieceInfo.x,pieceInfo.y,null);
                }
//
//                returnBitmap = Bitmap.createBitmap(resultP,width,height, Bitmap.Config.ARGB_8888);


                filterInfoResult.setFilterBitmap(returnBitmap);
                filterInfoResult.setType(FilterType.FACE_SKIN_VEINS);
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

    @Override
    public FacePart[] getFacePart() {
        //FacePart.FACE_NOSE_LEFT_RIGHT  //FACE_SKIN
        return new FacePart[]{FacePart.FACE_LEFT_RIGHT_AREA};
    }

    private class PieceInfo{
        private int x, y;

        private int [] piece;
        //绝对偏差总和
        private int avg;
        private int sumMinus;
        private int avgMinus;
        private float coarseness;

        public void createMinus(){
                for (int i = 0; i < piece.length; i++) {
                    int gray = Color.red(piece[i]);
                    gray = Math.abs(gray - avg);
                    sumMinus += gray;
                    piece[i] = Color.rgb(gray,gray,gray);
                }
                avgMinus = sumMinus /piece.length;
        }

        public Integer[] getPieceForInteger(){
            return ArrayUtils.toObject(piece);
        }

    }

}