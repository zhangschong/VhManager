package com.leon.tools.chart.stock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * K线元
 *
 * @author leon.zhang
 */
public class KDrawNode implements MaxMinNetBg.MaxMinNetNode {

    /**
     *
     */
    public float kOpeningPrice;
    /**
     * 收盘
     */
    public float kEndingPrice;
    /**
     */
    public float kMaxPrice;
    /**
     */
    public float kMinPrice;

    private Paint mPaint = new Paint();

    private boolean isDrawBgLine;

    /**
     * @param op
     * @param ep   收盘
     * @param maxp
     * @param minp
     */
    public KDrawNode(float op, float ep, float maxp, float minp) {
        changeData(op, ep, maxp, minp);
    }

    public void changeData(float op, float ep, float maxp, float minp) {
        this.kOpeningPrice = op;
        this.kEndingPrice = ep;
        this.kMaxPrice = maxp;
        this.kMinPrice = minp;
    }

    protected float getWidth(float maxWidth) {
        return maxWidth * 0.6f;
    }

    @Override
    public float getMax() {
        return kMaxPrice;
    }

    @Override
    public void drawElement(Canvas canvas, int i, float maxPrice, float minPrice,
                            float maxWidth, float maxHeight) {
        isDrawBgLine = ((i & 7) == 0);
        final float elementW = getWidth(maxWidth);
        final float r = -(maxWidth - elementW) / 2;
        final float l = r - elementW;
        final float dPrice = maxPrice - minPrice;
        mPaint.setStrokeWidth(1f);
        final float op = -(kOpeningPrice - minPrice) * maxHeight / dPrice;
        final float ep = -(kEndingPrice - minPrice) * maxHeight / dPrice;
        mPaint.setStyle(Style.FILL);
        // 绘制矩形�?
        if (kOpeningPrice > kEndingPrice) {
            mPaint.setColor(Color.RED);
            canvas.drawRect(l, op, r, ep, mPaint);
        } else if (kEndingPrice > kOpeningPrice) {
            mPaint.setColor(Color.GREEN);
            canvas.drawRect(l, ep, r, op, mPaint);
        } else {
            mPaint.setColor(Color.RED);
            canvas.drawLine(l, op, r, op, mPaint);
        }
        // 绘制中线
        final float tl = -(kMaxPrice - minPrice) * maxHeight / dPrice;
        final float bl = -(kMinPrice - minPrice) * maxHeight / dPrice;
        final float center = (r + l) / 2;
        canvas.drawLine(center, tl, center, bl, mPaint);
    }

    @Override
    public float getMin() {
        return kMinPrice;
    }

    @Override
    public boolean isDrawBgLine() {
        return isDrawBgLine;
    }
}