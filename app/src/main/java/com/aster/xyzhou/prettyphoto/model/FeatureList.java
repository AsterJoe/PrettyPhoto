package com.aster.xyzhou.prettyphoto.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/7 0007.
 */

public class FeatureList {
    public  List<String> feature = new ArrayList<>();

    public FeatureList() {
        feature.add("灰度效果");
        feature.add("底片效果");
        feature.add("怀旧效果");
        feature.add("去色效果");
        feature.add("高饱和度");
        feature.add("浮雕效果");
        feature.add("旗帜效果");
        feature.add("ggg");
        feature.add("hhh");
        feature.add("jjj");
    }

    public List<String> getFeatureList() {
        return feature;
    }
}
