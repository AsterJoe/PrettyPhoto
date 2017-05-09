package com.aster.xyzhou.prettyphoto.util;

/**
 * Created by Administrator on 2017/5/8 0008.
 */

public class ImageEffect {

    public final static float[] REVERSE_BITMAP = new float[]{
            -1, 0, 0, 1, 1,
            0, -1, 0, 1, 1,
            0, 0, -1, 1, 1,
            0, 0, 0, 1, 0
    };

    public final static float[] GRAY_EFFECT = new float[] {
            0.33F, 0.59F, 0.11F, 0, 0,
            0.33F, 0.59F, 0.11F, 0, 0,
            0.33F, 0.59F, 0.11F, 0, 0,
            0,     0,     0,     1, 0
    };

    public final static float[] OLD_EFFECT = new float[] {
            0.393F, 0.769F, 0.189F, 0, 0,
            0.349F, 0.686F, 0.168F, 0, 0,
            0.272F, 0.534F, 0.131F, 0, 0,
            0,      0,      0,      1, 0
    };

    public final static float[] QUSHE = new float[] {
            1.5F, 1.5F, 1.5F, 0, -1,
            1.5F, 1.5F, 1.5F, 0, -1,
            1.5F, 1.5F, 1.5F, 0, -1,
            0,    0,    0,    1,  0
    };

    public final static float[] GAOBAOHE = new float[] {
            1.438F, -0.122F, -0.016F, 0, -0.03F,
            -0.062F, 1.378F, -0.016F, 0, 0.05F,
            -0.062F, -0.122F, 1.483F, 0, -0.02F,
            0,       0,       0,      1,      0
    };
}
