package co.hoppen.filter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;

import java.util.Random;

import co.hoppen.cameralib.ErrorCode;
import co.hoppen.cameralib.HoppenCameraHelper;
import co.hoppen.cameralib.HoppenController;
import co.hoppen.cameralib.Instruction;
import co.hoppen.cameralib.OnButtonListener;
import co.hoppen.cameralib.OnDeviceListener;
import co.hoppen.cameralib.OnWaterListener;
import co.hoppen.cameralib.widget.UVCCameraTextureView;
import co.hoppen.filterlib.FilterHelper;
import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;
import co.hoppen.filterlib.OnFilterListener;

public class MainActivity extends AppCompatActivity implements OnDeviceListener, OnButtonListener, View.OnLongClickListener, OnFilterListener, OnWaterListener {
    private HoppenController controller;
    private UVCCameraTextureView uvc_camera;
    private ImageView iv_filter;

    private Bitmap bitmap;

    private FilterHelper filterHelper;

    private TextView tv_score,tv_res;

    private float res = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uvc_camera = findViewById(R.id.uvc_camera);
        controller = HoppenCameraHelper.createController(this, uvc_camera);
        controller.setDeviceButton(this);
        controller.setWaterListener(this);
        iv_filter = findViewById(R.id.iv_filter);
        iv_filter.setOnLongClickListener(this);
        filterHelper = new FilterHelper(this);

        tv_score = findViewById(R.id.tv_score);
        tv_res = findViewById(R.id.tv_res);

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnect(ErrorCode errorCode) {

    }

    @Override
    public void onButton(int state) {
        if (state==1){
            controller.sendInstructions(Instruction.WATER);
        }
    }

    public void toShui(View view){
        try {
            filterHelper.execute(FilterType.SKIN_HYDRATION_STATUS,bitmap,res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toYou(View view){
        try {
            filterHelper.execute(FilterType.SKIN_OIL_SECRETION,bitmap,res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toSe(View view){
        try {
            filterHelper.execute(FilterType.SKIN_PIGMENT_STATUS,bitmap,res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toHong(View view){
        try {
            filterHelper.execute(FilterType.SKIN_RED_BLOOD_STATUS,bitmap,res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toMao(View view){
        try {
            filterHelper.execute(FilterType.FOLLICLE_CLEAN_DEGREE,bitmap,res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toTan(View view){
        try {
            filterHelper.execute(FilterType.ELASTIC_FIBER_STATUS,bitmap,res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toJiao(View view){
        try {
            filterHelper.execute(FilterType.COLLAGEN_FIBERS_STATUS,bitmap,res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test(View view){
        try {
            filterHelper.execute(FilterType.TEST,bitmap,res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        iv_filter.setImageBitmap(null);
        return false;
    }

    @Override
    public void OnFilter(FilterInfoResult filterInfoResult) {
        if (filterInfoResult.getStatus()== FilterInfoResult.Status.SUCCESS){
            iv_filter.setImageBitmap(filterInfoResult.getFilterBitmap());
            tv_score.setText(filterInfoResult.getScore()+"");
            LogUtils.e(filterInfoResult.toString());
        }
    }

    @Override
    public void onWaterCallback(float water) {
        res = water;
        tv_res.setText(water+"");
        bitmap = uvc_camera.getBitmap(640, 480);
        iv_filter.setImageBitmap(bitmap);
    }
}