package com.leon.tools.chart.stock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;

import com.leon.tools.chart.BaseElement;
import com.leon.tools.chart.PaintStyle;

import java.util.ArrayList;

/**
 * Created by kutear.guo on 2016/3/23.
 */
public class LineElement extends BaseElement {
    private float mMax = 1000;
    private float mMin;
    private RectF mContentArea = new RectF();
    private ArrayList<ArrayList<Float>> mLines = new ArrayList<ArrayList<Float>>();
    private float mCountNum;//点数
    private float mUnitsWidth;//间隔
    private float mRadioY;//Y轴的比例
    private PaintStyle.LineStyle[] mLineStyle;
    private Runnable mCalculateRunnable = new Runnable() {

        @Override
        public void run() {
            calculateMaxAndMin();
            mContentArea.set(mPaddingRect.l, mPaddingRect.t, getWidth()
                    - mPaddingRect.r, getHeight() - mPaddingRect.b);
            mCountNum = (mLines != null && mLines.size() > 0) ? mLines.get(0).size() : 100;
            mUnitsWidth = mContentArea.width() / (mCountNum - 1);
            mRadioY = (mContentArea.height()) / (mMax - mMin);
            postInvalidate();
        }
    };

    /**
     * 设置线条颜色
     *
     * @param lineStyle
     */
    public void setLineColor(PaintStyle.LineStyle... lineStyle) {
        this.mLineStyle = lineStyle;
        postRunnable(mCalculateRunnable);
    }

    /**
     * 设置多条数据
     *
     * @param line
     */
    public void setLines(ArrayList<ArrayList<Float>> line) {
        this.mLines = line;
        postRunnable(mCalculateRunnable);
    }

    /**
     * 获取第几个点的横坐标
     * 相对于mContentArea左下为原
     *
     * @param index
     * @return
     */
    private float getXPointAt(int index) {
        return mContentArea.width() - index * mUnitsWidth;
    }

    /**
     * 获取第几个点的纵坐标
     * 相对于mContentArea左下为原
     *
     * @param index
     * @return
     */
    private float getYPointAt(int index, int lineIndex) {
        float tempY = mLines.get(lineIndex).get(index) - mMin;
        tempY = -tempY;
        tempY = tempY * mRadioY;
        return tempY;
    }

    /**
     * 根据现有数据计算极
     */
    private void calculateMaxAndMin() {
        mMin = Float.MAX_VALUE;
        mMax = Float.MIN_VALUE;
        for (ArrayList<Float> line : mLines) {
            for (Float point : line) {
                if (point > mMax) {
                    mMax = point;
                }
                if (point < mMin) {
                    mMin = point;
                }
            }
        }
    }


//    /**
//     *
//     * @param min
//     * @param max
//     */
//    public void setMaxAndMin(float min, float max) {
//        this.mMin = min;
//        this.mMax = max;
//        postRunnable(mCalculateRunnable);
//    }

    @Override
    protected void onLayoutChanged(boolean isChanged, float left, float top, float right, float bottom) {
        if (isChanged) {
            postRunnable(mCalculateRunnable);
        }
    }

    @Override
    protected void onDraw(Canvas canvas, RectF canDrawRect) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//        paint.setColor(Color.RED);
//        canvas.drawRect(mContentArea, paint);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(3);
        canvas.save();
        canvas.translate(0 + mPaddingRect.l, mContentArea.bottom);
        for (int i = 0; i < mLines.size(); i++) {
            drawLine(canvas, i, paint);
        }
        canvas.restore();
    }

    /**
     * 获取线条绘制
     *
     * @param index
     * @return
     */
    private PaintStyle.LineStyle getLineStyle(int index) {
        return (mLineStyle != null && mLineStyle.length > index) ? mLineStyle[index] :
                new PaintStyle.LineStyle(Color.GRAY, 3);
    }

    private void drawLine(Canvas canvas, int index, Paint paint) {
        PaintStyle.LineStyle style = getLineStyle(index);
        style.setPaint(paint);
        for (int i = 0; i < mLines.get(index).size() - 1; i++) {
            canvas.drawLine(getXPointAt(i), getYPointAt(i, index),
                    getXPointAt(i + 1), getYPointAt(i + 1, index), paint);
        }
    }

    @Override
    protected void onCreate() {

    }
}
