package com.aster.xyzhou.prettyphoto.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/5/7 0007.
 */

public class SquareView extends ImageView {

    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int squareSize = width > height ? height : width;
        setMeasuredDimension(squareSize, squareSize);
    }
}
