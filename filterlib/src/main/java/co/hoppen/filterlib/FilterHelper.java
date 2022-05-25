package co.hoppen.filterlib;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;

import org.opencv.android.OpenCVLoader;

import java.lang.reflect.Constructor;

import co.hoppen.filterlib.filter.FaceFilter;
import co.hoppen.filterlib.filter.Filter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by YangJianHui on 2021/9/11.
 */
public class FilterHelper {
    private OnFilterListener onFilterListener;
    private MLFaceAnalyzer analyzer;

    public FilterHelper(OnFilterListener onFilterListener){
        this.onFilterListener = onFilterListener;
        if (!OpenCVLoader.initDebug()) {
            LogUtils.e("Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            LogUtils.e("OpenCV library found inside package. Using it!");
        }
    }

    public void execute(FilterType type, Bitmap bitmap , float resistance)throws Exception{
        new Thread(){
            @Override
            public void run() {
                try {
                    Filter filter = createFilter(type, bitmap, resistance);
                    if (filter instanceof FaceFilter){
                        if (analyzer==null){
                            analyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(new MLFaceAnalyzerSetting.Factory()
                                    // 设置是否检测人脸关键点。
                                    .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                                    // 设置是否检测人脸特征和表情。
                                    .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
                                    // 设置仅启用人脸表情检测和性别检测。
                                    .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURE_EMOTION | MLFaceAnalyzerSetting.TYPE_FEATURE_GENDAR)
                                    // 设置是否检测人脸轮廓点。
                                    .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                                    // 设置是否开启人脸追踪并指定快捷追踪模式。
                                    .setTracingAllowed(false, MLFaceAnalyzerSetting.MODE_TRACING_FAST)
                                    // 设置检测器速度/精度模式。
                                    .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                                    // 设置是否开启Pose检测（默认开启）。
                                    .setPoseDisabled(false)
                                    .create());
                        }
                        boolean finish = filter.facePositioning(analyzer);
                        if (finish){
                            executeResult(filter.onFilter());
                        }
                    }else {
                        executeResult(filter.onFilter());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void executeResult(FilterInfoResult filterInfoResult){
        Observable.just(filterInfoResult)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FilterInfoResult>() {
                    @Override
                    public void accept(FilterInfoResult filterInfoResult) throws Throwable {
                        onFilterListener.onFilter(filterInfoResult);
                    }
                });
    }

    private <F extends Filter> F createFilter(FilterType type, Bitmap bitmap , float resistance)throws Exception{
        Class<? extends Filter> filterClass = null;
        F f = null;

        filterClass = type.getType();

        if (filterClass!=null){
            Constructor<? extends Filter> declaredConstructor = filterClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            f = (F) declaredConstructor.newInstance();
            f.setOriginalImage(bitmap).setResistance(resistance);
        }
        return f;
    }
}
