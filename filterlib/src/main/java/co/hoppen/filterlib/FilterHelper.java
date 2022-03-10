package co.hoppen.filterlib;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.OpenCVLoader;

import java.lang.reflect.Constructor;

import co.hoppen.filterlib.filter.CollagenStatus;
import co.hoppen.filterlib.filter.ElasticFiberStatus;
import co.hoppen.filterlib.filter.FaceHydrationStatus;
import co.hoppen.filterlib.filter.FaceOilSecretion;
import co.hoppen.filterlib.filter.FaceSkinEpidermisSpots;
import co.hoppen.filterlib.filter.FaceSkinWrinkle;
import co.hoppen.filterlib.filter.Filter;
import co.hoppen.filterlib.filter.FollicleCleanDegree;
import co.hoppen.filterlib.filter.SkinHydrationStatus;
import co.hoppen.filterlib.filter.SkinOilSecretion;
import co.hoppen.filterlib.filter.SkinPigmentStatus;
import co.hoppen.filterlib.filter.SkinRedBloodStatus;
import co.hoppen.filterlib.filter.TestFilter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by YangJianHui on 2021/9/11.
 */
public class FilterHelper {
    private OnFilterListener onFilterListener;

    public FilterHelper(OnFilterListener onFilterListener){
        this.onFilterListener = onFilterListener;
        if (!OpenCVLoader.initDebug()) {
            LogUtils.e("Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            LogUtils.e("OpenCV library found inside package. Using it!");
        }

    }

    public void execute(FilterType type, Bitmap bitmap , float resistance)throws Exception{
//        Observable.just(createFilter(type, bitmap, resistance).onFilter())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(filterInfoResult -> onFilterListener.OnFilter(filterInfoResult));

        new Thread(){
            @Override
            public void run() {
                try {
                    Filter filter = createFilter(type, bitmap, resistance);
                    FilterInfoResult filterInfoResult = filter.onFilter();

                    Observable.just(filterInfoResult).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<FilterInfoResult>() {
                                @Override
                                public void accept(FilterInfoResult filterInfoResult) throws Throwable {
                                    onFilterListener.OnFilter(filterInfoResult);
                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private <F extends Filter> F createFilter(FilterType type, Bitmap bitmap , float resistance)throws Exception{
        Class<? extends Filter> filterClass = null;
        F f = null;
        switch (type){
            case SKIN_HYDRATION_STATUS:
                filterClass = SkinHydrationStatus.class;
                break;
            case SKIN_OIL_SECRETION:
                filterClass = SkinOilSecretion.class;
                break;
            case SKIN_PIGMENT_STATUS:
                filterClass = SkinPigmentStatus.class;
                break;
            case SKIN_RED_BLOOD_STATUS:
                filterClass = SkinRedBloodStatus.class;
                break;
            case FOLLICLE_CLEAN_DEGREE:
                filterClass = FollicleCleanDegree.class;
                break;
            case ELASTIC_FIBER_STATUS:
                filterClass = ElasticFiberStatus.class;
                break;
            case COLLAGEN_STATUS:
                filterClass = CollagenStatus.class;
                break;
            case TEST:
                filterClass = TestFilter.class;
                break;
            case FACE_HYDRATION_STATUS:
                filterClass = FaceHydrationStatus.class;
                break;
            case FACE_OIL_SECRETION:
                filterClass = FaceOilSecretion.class;
                break;
            case FACE_SKIN_EPIDERMIS_SPOTS:
                filterClass = FaceSkinEpidermisSpots.class;
                break;
            case FACE_SKIN_WRINKLE:
                filterClass = FaceSkinWrinkle.class;
                break;
        }
        if (filterClass!=null){
            Constructor<? extends Filter> declaredConstructor = filterClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            f = (F) declaredConstructor.newInstance();
            f.setOriginalImage(bitmap).setResistance(resistance);
        }
        return f;
    }
}
