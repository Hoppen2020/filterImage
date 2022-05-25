package co.hoppen.filterlib.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.huawei.hms.mlsdk.face.MLFace;

import co.hoppen.filterlib.FacePart;

/**
 * Created by YangJianHui on 2022/4/7.
 */
public class CutoutUtils {

    /**
     *
     * @param src 原图
     * @param faceParts 需要的部位
     * @param face
     * @return
     */
    public static Bitmap cutoutPart(Bitmap src, FacePart[] faceParts, MLFace face){
        Bitmap canvasBitmap = Bitmap.createBitmap(src.getWidth(),src.getHeight(),src.getConfig());
        Canvas canvas = new Canvas(canvasBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        for (int i = 0; i < faceParts.length; i++) {
            FacePart facePart = faceParts[i];
            switch (facePart){
                case FACE_T:
                    cutoutT(face,canvas,paint);
                    break;
                case FACE_SKIN:

                    break;
                case FACE_EYE_BOTTOM:
                    cutoutEyeBottom(face,canvas,paint);
                    break;
                case FACE_LEFT_RIGHT_AREA:
                    cutoutLeftRightFace(face,canvas,paint);
                    break;
                case FACE_NOSE:
                    cutoutNose(face,canvas,paint);
                    break;
                case FACE_NOSE_LEFT_RIGHT:
                    cutoutNoseLeftRight(face,canvas,paint);
                    break;
                case FACE_FOREHEAD:
                    cutoutForehead(face,canvas,paint);
                    break;
                case FACE_MIDDLE:
                    cutoutMiddle(face,canvas,paint);
                    break;
            }
        }
        int width = src.getWidth();
        int height = src.getHeight();

        int [] srcOriginalPixels = new int[width * height];
        int [] canvasPixels = new int[width * height];
        int [] resultPixels = new int[width * height];
        src.getPixels(srcOriginalPixels,0,width,0,0,width,height);
        canvasBitmap.getPixels(canvasPixels,0,width,0,0,width,height);

        for (int i = 0; i < canvasPixels.length; i++) {
            int pixel =  canvasPixels[i];
            int alpha = Color.alpha(pixel);
            if (alpha==0) {
                resultPixels[i] = 0;
            }else {
                resultPixels[i] = srcOriginalPixels[i];
            }
        }
        canvasBitmap.setPixels(resultPixels,0,width, 0, 0, width, height);
        return canvasBitmap;
    }

    private static void cutoutNoseLeftRight(MLFace face, Canvas canvas, Paint paint) {
        Bitmap facePartLeft = BitmapUtils.getImageFromAssetsFile("FacePartLeft.png");
        RectF facePartLeftRectF = new RectF();
        facePartLeftRectF.left = face.getAllPoints().get(47).getX();
        facePartLeftRectF.top = face.getAllPoints().get(51).getY();
        facePartLeftRectF.right = face.getAllPoints().get(311).getX();
        facePartLeftRectF.bottom = face.getAllPoints().get(40).getY();
        canvas.drawBitmap(facePartLeft,null,facePartLeftRectF,paint);

        Bitmap facePartRight = BitmapUtils.getImageFromAssetsFile("FacePartRight.png");
        RectF facePartRightRectF = new RectF();
        facePartRightRectF.left = face.getAllPoints().get(375).getX();
        facePartRightRectF.top = face.getAllPoints().get(113).getY();
        facePartRightRectF.right = face.getAllPoints().get(109).getX();
        facePartRightRectF.bottom = face.getAllPoints().get(102).getY();
        canvas.drawBitmap(facePartRight,null,facePartRightRectF,paint);
    }

    private static void cutoutEyeBottom(MLFace face, Canvas canvas, Paint paint) {
        Bitmap eyeLeft = BitmapUtils.getImageFromAssetsFile("EyeLeftBottom+.png");
        RectF eyeLeftRectF = new RectF();
        eyeLeftRectF.left = face.getAllPoints().get(845).getX();//46
        eyeLeftRectF.top = face.getAllPoints().get(845).getY();//263
        eyeLeftRectF.right = face.getAllPoints().get(846).getX();//846
        eyeLeftRectF.bottom = face.getAllPoints().get(287).getY();//271
        canvas.drawBitmap(eyeLeft,null,eyeLeftRectF,paint);

        Bitmap eyeRight = BitmapUtils.getImageFromAssetsFile("EyeRightBottom+.png");
        RectF eyeRightRectF = new RectF();
        eyeRightRectF.left = face.getAllPoints().get(847).getX();//847
        eyeRightRectF.top = face.getAllPoints().get(848).getY();//135
        eyeRightRectF.right = face.getAllPoints().get(848).getX();//107
        eyeRightRectF.bottom = face.getAllPoints().get(351).getY();//115
        canvas.drawBitmap(eyeRight,null,eyeRightRectF,paint);

    }

    private static void cutoutMiddle(MLFace face, Canvas canvas, Paint paint) {
        Bitmap middle = BitmapUtils.getImageFromAssetsFile("Middle.png");
        RectF middleRectF = new RectF();
        middleRectF.left = face.getAllPoints().get(48).getX();
        middleRectF.top = face.getAllPoints().get(424).getY();
        middleRectF.right = face.getAllPoints().get(110).getX();
        middleRectF.bottom = face.getAllPoints().get(450).getY();
        canvas.drawBitmap(middle,null,middleRectF,paint);
    }

    private static void cutoutForehead(MLFace face,Canvas canvas,Paint paint){
        Bitmap forehead = BitmapUtils.getImageFromAssetsFile("Forehead.png");
        RectF foreheadRectF = new RectF();
        foreheadRectF.left = face.getAllPoints().get(240).getX();
        foreheadRectF.top = face.getAllPoints().get(200).getY();
        foreheadRectF.right = face.getAllPoints().get(158).getX();
        foreheadRectF.bottom = face.getAllPoints().get(404).getY();
        canvas.drawBitmap(forehead,null,foreheadRectF,paint);
    }


    private static void cutoutT(MLFace face,Canvas canvas,Paint paint){
        Bitmap tForehead = BitmapUtils.getImageFromAssetsFile("TForehead+.png");
        RectF foreheadRectF = new RectF();
        foreheadRectF.left = face.getAllPoints().get(240).getX();
        foreheadRectF.top = face.getAllPoints().get(200).getY();
        foreheadRectF.right = face.getAllPoints().get(158).getX();
        foreheadRectF.bottom = face.getAllPoints().get(399).getY();
        Bitmap tNose = BitmapUtils.getImageFromAssetsFile("TNose+.png");
        RectF noseRectF = new RectF();
        noseRectF.left = face.getAllPoints().get(312).getX();
        noseRectF.top = face.getAllPoints().get(401).getY();
        noseRectF.right = face.getAllPoints().get(376).getX();
        noseRectF.bottom = face.getAllPoints().get(455).getY();
        canvas.drawBitmap(tForehead,null,foreheadRectF,paint);
        canvas.drawBitmap(tNose,null,noseRectF,paint);
    }

    private static void cutoutLeftRightFace(MLFace face,Canvas canvas,Paint paint){
        Bitmap leftFace = BitmapUtils.getImageFromAssetsFile("LeftFace+.png");
        RectF leftRectF = new RectF();
        leftRectF.left = face.getAllPoints().get(264).getX();
        leftRectF.right = face.getAllPoints().get(846).getX();
        leftRectF.top = face.getAllPoints().get(264).getY();
        leftRectF.bottom = face.getAllPoints().get(35).getY();
        Bitmap rightFace = BitmapUtils.getImageFromAssetsFile("RightFace+.png");
        RectF rightRectF = new RectF();
        rightRectF.right = face.getAllPoints().get(135).getX();
        rightRectF.left = face.getAllPoints().get(847).getX();
        rightRectF.top = face.getAllPoints().get(135).getY();
        rightRectF.bottom = face.getAllPoints().get(97).getY();
        canvas.drawBitmap(leftFace,null,leftRectF,paint);
        canvas.drawBitmap(rightFace,null,rightRectF,paint);
    }

    private static void cutoutNose(MLFace face,Canvas canvas,Paint paint){
        Bitmap tNose = BitmapUtils.getImageFromAssetsFile("Nose.png");
        RectF noseRectF = new RectF();
        noseRectF.left = face.getAllPoints().get(312).getX();
        noseRectF.top = face.getAllPoints().get(418).getY();
        noseRectF.right = face.getAllPoints().get(376).getX();
        noseRectF.bottom = face.getAllPoints().get(455).getY();
        canvas.drawBitmap(tNose,null,noseRectF,paint);
    }

}
