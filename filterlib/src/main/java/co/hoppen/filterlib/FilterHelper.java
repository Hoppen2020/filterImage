package co.hoppen.filterlib;

import android.graphics.Bitmap;

import java.lang.reflect.Constructor;

import co.hoppen.filterlib.filter.CollagenFibersStatus;
import co.hoppen.filterlib.filter.ElasticFiberStatus;
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
    }

    public void execute(FilterType type, Bitmap bitmap , float resistance)throws Exception{
        Filter filter = createFilter(type, bitmap, resistance);
        if (filter!=null){
            Observable.just(filter.onFilter())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(filterInfoResult -> onFilterListener.OnFilter(filterInfoResult));
        }
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
            case COLLAGEN_FIBERS_STATUS:
                filterClass = CollagenFibersStatus.class;
                break;
            case TEST:
                filterClass = TestFilter.class;
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
