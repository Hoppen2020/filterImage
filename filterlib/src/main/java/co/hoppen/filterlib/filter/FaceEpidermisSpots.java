package co.hoppen.filterlib.filter;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;

import co.hoppen.filterlib.FilterInfoResult;
import co.hoppen.filterlib.FilterType;

/**
 * Created by YangJianHui on 2022/3/7.
 */
public class FaceEpidermisSpots extends Filter{

    @Override
    public FilterInfoResult onFilter() {
        FilterInfoResult filterInfoResult = new FilterInfoResult();
        try {
            Bitmap originalImage = getOriginalImage();
            if (!isEmptyBitmap(originalImage)){
                Bitmap faceSkin = getFaceSkin();


                filterInfoResult.setFilterBitmap(faceSkin);
                filterInfoResult.setType(FilterType.FACE_EPIDERMIS_SPOTS);
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

}