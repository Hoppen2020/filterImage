package co.hoppen.filterlib;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;

import org.opencv.android.OpenCVLoader;

import java.lang.reflect.Constructor;

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
