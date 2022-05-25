package co.hoppen.filterlib.filter;

import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;

import co.hoppen.filterlib.FacePart;

/**
 * Created by YangJianHui on 2022/4/7.
 */
public interface FaceFilter {
    FacePart[] getFacePart();
}
