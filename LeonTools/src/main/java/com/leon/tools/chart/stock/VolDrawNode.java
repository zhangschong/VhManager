package com.leon.tools.chart.stock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;


/**
 * 成交量大小
 *
 * @author leon.zhang
 */
public class VolDrawNode implements MaxMinElement.IMaxMinDrawNode {

    private Paint mPaint = new Paint();

    private int mCurrentVol;

    public VolDrawNode(int vol) {
        mCurrentVol = vol;
    }

    @Override
    public float getMax() {
        return mCurrentVol;
    }

    protected float getWidth(float maxWidth) {
        return 0.6f * maxWidth;
    }

    @Override
    public void drawElement(Canvas canvas, int index, float max, float min,
                            float maxWidth, float maxHeight) {
        final float elementW = getWidth(maxWidth);
        final float r = -(maxWidth - elementW) / 2;
        final float l = r - elementW;
        final float t = -(mCurrentVol - min) * maxHeight / max;
        final float b = 0f;
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.RED);
        canvas.drawRect(l, t, r, b, mPaint);
    }

    @Override
    public float getMin() {
        return 0;
    }

}
