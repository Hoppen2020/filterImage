package co.hoppen.filterlib;

import co.hoppen.filterlib.filter.CollagenStatus;
import co.hoppen.filterlib.filter.ElasticFiberStatus;
import co.hoppen.filterlib.filter.FaceAcne;
import co.hoppen.filterlib.filter.FaceAcne2;
import co.hoppen.filterlib.filter.FaceAcne3;
import co.hoppen.filterlib.filter.FaceAcne4;
import co.hoppen.filterlib.filter.FaceBlackHeads;
import co.hoppen.filterlib.filter.FaceBrownArea;
import co.hoppen.filterlib.filter.FaceDarkCircles;
import co.hoppen.filterlib.filter.FaceEpidermisSpots;
import co.hoppen.filterlib.filter.FaceFollicleCleanDegree;
import co.hoppen.filterlib.filter.FaceHornyPlug;
import co.hoppen.filterlib.filter.FaceHydrationStatus;
import co.hoppen.filterlib.filter.FaceOilSecretion;
import co.hoppen.filterlib.filter.FacePorphyrin;
import co.hoppen.filterlib.filter.FaceRedBlood;
import co.hoppen.filterlib.filter.FaceSensitive;
import co.hoppen.filterlib.filter.FaceSkinVeins;
import co.hoppen.filterlib.filter.FaceSkinVeins2;
import co.hoppen.filterlib.filter.FaceSuperficialPlaque;
import co.hoppen.filterlib.filter.FaceTest2;
import co.hoppen.filterlib.filter.FaceWrinkle;
import co.hoppen.filterlib.filter.FaceWrinkle4;
import co.hoppen.filterlib.filter.FaceWrinkle5;
import co.hoppen.filterlib.filter.FaceWrinkle6;
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

    //全脸——水分
    FACE_HYDRATION_STATUS(FaceHydrationStatus.class),
    //全脸——表皮斑
    FACE_EPIDERMIS_SPOTS(FaceEpidermisSpots.class),
    //全脸——敏感
    FACE_SENSITIVE(FaceSensitive.class),
    //全脸——卟啉（伍氏）
    FACE_PORPHYRIN(FacePorphyrin.class),
    //全脸——浅层斑（uv）
    FACE_SUPERFICIAL_PLAQUE(FaceSuperficialPlaque.class),
    //全脸——角质栓
    FACE_HORNY_PLUG(FaceHornyPlug.class),
    //全脸——油分
    FACE_OIL_SECRETION(FaceOilSecretion.class),
    //全脸——皱纹
    FACE_WRINKLE(FaceWrinkle6.class),
    //全脸——毛孔
    FACE_FOLLICLE_CLEAN_DEGREE(FaceFollicleCleanDegree.class),
    //2022 03 28
    //全脸——黑眼圈
    Face_Dark_Circles(FaceDarkCircles.class),
    //全脸——痤疮
    FACE_ACNE(FaceAcne4.class),
    //全脸——皮肤纹理
    FACE_SKIN_VEINS(FaceSkinVeins.class),
    //全脸——测试
    FACE_TEST(FaceTest2.class),
    //棕色区
    FACE_BROWN_AREA(FaceBrownArea.class),
    //红色区
    FACE_RED_BLOOD(FaceRedBlood.class),
    //黑头
    FACE_BLACK_HEADS(FaceBlackHeads.class),


    TEST(TestFilter.class);

    private Class<? extends Filter> type;

    FilterType(Class<? extends Filter> type){
        this.type = type;
    }

    public Class<? extends Filter> getType() {
        return type;
    }
}
