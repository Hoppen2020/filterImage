package co.hoppen.filter;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

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
import co.hoppen.filterlib.filter.SkinPigmentStatus;
import co.hoppen.filterlib.filter.SkinRedBloodStatus;

public class MainActivity extends AppCompatActivity implements OnDeviceListener, OnButtonListener, View.OnLongClickListener, OnFilterListener, OnWaterListener {
    private HoppenController controller;
    private UVCCameraTextureView uvc_camera;
    private ImageView iv_filter;

    private Bitmap bitmap;

    private Bitmap filterBitmap;

    private FilterHelper filterHelper;

    private FilterInfoResult result;

    private TextView tv_res,tv_auto,tv_gray,tv_percentage,tv_depth,tv_score;

    private EditText et_score;

    private float res = 0;

    boolean isFilter = false;

    private SeekBar sb_seek,sb_gray;

    private View fl_loading;

    private boolean touchable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        uvc_camera = findViewById(R.id.uvc_camera);
        controller = HoppenCameraHelper.createController(this, uvc_camera);
        controller.setDeviceButton(this);
        controller.setWaterListener(this);
        iv_filter = findViewById(R.id.iv_filter);
        iv_filter.setOnLongClickListener(this);
        filterHelper = new FilterHelper(this);

        fl_loading = findViewById(R.id.fl_loading);

        tv_res = findViewById(R.id.tv_res);
        sb_seek = findViewById(R.id.sb_seek);
        sb_gray = findViewById(R.id.sb_gray);

        tv_auto = findViewById(R.id.tv_auto);
        tv_gray = findViewById(R.id.tv_gray);
        //1634884872575 1635128298648
        //bitmap = ImageUtils.getBitmap(new File(Environment.getExternalStorageDirectory().getPath()+"/filterImage","1636099857029.jpg"));

        tv_percentage = findViewById(R.id.tv_percentage);
        tv_depth = findViewById(R.id.tv_depth);
        tv_score = findViewById(R.id.tv_score);
        et_score = findViewById(R.id.et_score);

        sb_seek.setMax(200);//色素测试
        sb_seek.setProgress(0);

        sb_gray.setMax(200);//
        sb_gray.setProgress(SkinRedBloodStatus.COLOR_GAMUT);

        tv_auto.setText(SkinPigmentStatus.TEST+"");
        tv_gray.setText(SkinRedBloodStatus.COLOR_GAMUT+"");


        sb_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float a =  progress - 100f;
                tv_auto.setText(a+"");
                SkinPigmentStatus.TEST = (int) a;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                toSe(null);
            }
        });

        sb_gray.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_gray.setText(progress+"");
                SkinRedBloodStatus.COLOR_GAMUT = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                toHong(null);
            }
        });



//        bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/512.jpg");
//        iv_filter.setImageBitmap(bitmap);
//        isFilter = false;
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
        execute(FilterType.SKIN_HYDRATION_STATUS,bitmap,res);
    }

    public void toYou(View view){
        execute(FilterType.SKIN_OIL_SECRETION,bitmap,res);
    }

    public void toSe(View view){
        execute(FilterType.SKIN_PIGMENT_STATUS,bitmap,res);
    }

    public void toHong(View view){
        execute(FilterType.SKIN_RED_BLOOD_STATUS,bitmap,res);
    }

    public void toMao(View view){
        execute(FilterType.FOLLICLE_CLEAN_DEGREE,bitmap,res);
    }

    public void toTan(View view){
        execute(FilterType.ELASTIC_FIBER_STATUS,bitmap,res);
    }

    public void toJiao(View view){
        execute(FilterType.COLLAGEN_STATUS,bitmap,res);
    }

    public void test(View view){
        execute(FilterType.TEST,bitmap,res);
    }

    private void execute(FilterType type, Bitmap bitmap , float resistance){
        try {
            fl_loading.setVisibility(View.VISIBLE);
            touchable = false;
            filterHelper.execute(type,bitmap,resistance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void es(View view){

        ResolveInfo rInfo = null;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> mApps = Utils.getApp().getPackageManager().queryIntentActivities(
                mainIntent, 0);
        for (ResolveInfo resolveInfo : mApps) {
            if (resolveInfo.activityInfo.packageName.equals("com.estrongs.android.pop")){
                rInfo = resolveInfo;
                break;
            }
            String nam = (resolveInfo.activityInfo
                    .loadLabel(Utils.getApp().getPackageManager()) + "").trim();
            if (nam.indexOf("文件") != -1) {
                rInfo = resolveInfo;
                break;
            }
        }
        if (rInfo != null) {
            mainIntent.setComponent(new ComponentName(
                    rInfo.activityInfo.packageName, rInfo.activityInfo.name));
            startActivity(mainIntent);
        }
    }

    public void show(View view){
        if (!isFilter){
            if (filterBitmap!=null){
                iv_filter.setImageBitmap(filterBitmap);
                isFilter = true;
            }
        }else {
            if (bitmap!=null){
                iv_filter.setImageBitmap(bitmap);
                isFilter = false;
            }
        }
    }

    public void saveFilter(View view){
        save(filterBitmap);
    }

    public void save(Bitmap bitmap){
        XXPermissions.with(this).permission(Permission.Group.STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all){
                    if (bitmap!=null){
                        File path = new File(Environment.getExternalStorageDirectory(), "filterImage");
                        if (!path.exists()){
                            path.mkdirs();
                        }
                        try {
                            String name = "";
                            if (result!=null){
                                 name = System.currentTimeMillis() +"_"+ result.getType().toString() +"_电阻："+result.getResistance()+"_占比："+result.getRatio()+
                                        "_深浅:"+result.getDepth()+"_打分:"+et_score.getText().toString()+"_原始："+result.getScore();
                            }
                            File file = new File(path,name.equals("")?"test":name+".jpg");
                            LogUtils.e("save "+file.getName());
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                            fos.flush();
                            fos.close();

                            Toast.makeText(MainActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                        }
                    }
                }
            }
            @Override
            public void onDenied(List<String> permissions, boolean never) {
                if (never)XXPermissions.startPermissionActivity(MainActivity.this);
            }
        });
    }

    public void save(View view){
        save(bitmap);
    }

    @Override
    public boolean onLongClick(View v) {
        iv_filter.setImageBitmap(null);
        isFilter = false;
        return true;
    }



    @Override
    public void onFilter(FilterInfoResult filterInfoResult) {
        if (filterInfoResult.getStatus()== FilterInfoResult.Status.SUCCESS){
            isFilter = true;
            filterBitmap = filterInfoResult.getFilterBitmap();
            result = filterInfoResult;
            iv_filter.setImageBitmap(filterBitmap);
            //tv_score.setText(filterInfoResult.getScore()+"");
            tv_percentage.setText(""+filterInfoResult.getRatioString());
            tv_depth.setText(""+filterInfoResult.getDepth());
            tv_score.setText(""+filterInfoResult.getScore()+"");
            LogUtils.e(filterInfoResult.toString());
        }
        fl_loading.setVisibility(View.GONE);
        touchable = true;
    }

    @Override
    public void onWaterCallback(float water) {
        res = water;
        tv_res.setText(water+"");
        bitmap = uvc_camera.getBitmap(640, 480);
        iv_filter.setImageBitmap(bitmap);
        isFilter = false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!touchable) return touchable;
        return super.dispatchTouchEvent(ev);
    }
}