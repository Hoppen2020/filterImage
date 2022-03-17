package co.hoppen.filterlib;

import co.hoppen.filterlib.filter.CollagenStatus;
import co.hoppen.filterlib.filter.ElasticFiberStatus;
import co.hoppen.filterlib.filter.FaceEpidermisSpots;
import co.hoppen.filterlib.filter.FaceHornyPlug;
import co.hoppen.filterlib.filter.FaceHydrationStatus;
import co.hoppen.filterlib.filter.FaceOilSecretion;
import co.hoppen.filterlib.filter.FacePorphyrin;
import co.hoppen.filterlib.filter.FaceSensitive;
import co.hoppen.filterlib.filter.FaceSuperficialPlaque;
import co.hoppen.filterlib.filter.FaceWrinkle;
import co.hoppen.filterlib.filter.Filter;
import co.hoppen.filterlib.filter.FollicleCleanDegree;
import co.hoppen.filterlib.filter.SkinHydrationStatus;
import co.hoppen.filterlib.filter.SkinOilSecretion;
import co.hoppen.filterlib.filter.SkinPigmentStatus;
import co.hoppen.filterlib.filter.SkinRedBloodStatus;
import co.hoppen.filterlib.filter.TestFilter;

/**
 * Created by YangJianHui on 2021/9/10.
 */
public enum FilterType {
    SKIN_HYDRATION_STATUS(SkinHydrationStatus.class),
    SKIN_OIL_SECRETION(SkinOilSecretion.class),
    SKIN_PIGMENT_STATUS(SkinPigmentStatus.class),
    SKIN_RED_BLOOD_STATUS(SkinRedBloodStatus.class),
    FOLLICLE_CLEAN_DEGREE(FollicleCleanDegree.class),
    ELASTIC_FIBER_STATUS(ElasticFiberStatus.class),
    COLLAGEN_STATUS(CollagenStatus.class),

    FACE_HYDRATION_STATUS(FaceHydrationStatus.class),
    FACE_EPIDERMIS_SPOTS(FaceEpidermisSpots.class),



    FACE_SENSITIVE(FaceSensitive.class),
    FACE_PORPHYRIN(FacePorphyrin.class),
    FACE_SUPERFICIAL_PLAQUE(FaceSuperficialPlaque.class),
    FACE_HORNY_PLUG(FaceHornyPlug.class),
    FACE_OIL_SECRETION(FaceOilSecretion.class),
    FACE_WRINKLE(FaceWrinkle.class),
    TEST(TestFilter.class);


    private Class<? extends Filter> type;

    FilterType(Class<? extends Filter> type){
        this.type = type;
    }

    public Class<? extends Filter> getType() {
        return type;
    }
}
