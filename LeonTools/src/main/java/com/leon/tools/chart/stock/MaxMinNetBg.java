package com.leon.tools.chart.stock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.leon.tools.chart.PaintStyle;
import com.leon.tools.chart.Zoom;


/**
 * Created by leon.zhang on 2016/3/25.
 */
public class MaxMinNetBg<E extends MaxMinNetBg.MaxMinNetNode> extends MaxMinElementBg<E> {
    private Paint mPaint = new Paint();
    private int mHorizontalCounts = 4;
    private int mVerticalItemCounts = 4;

    private float mHeightSkip;
    private PaintStyle mHorizontalLineStyle;
    private PaintStyle mVertialLineStyle;
    private PaintStyle mRectStyle;
    private Path mPath = new Path();

    /**
     * 设置线条的样
     *
     * @param horizontalLineStyle
     * @param verticalStyle
     */
    public void setLineStyle(PaintStyle horizontalLineStyle,
                             PaintStyle verticalStyle) {
        mHorizontalLineStyle = horizontalLineStyle;
        mVertialLineStyle = verticalStyle;
        if (null != mElement)
            mElement.postInvalidate();
    }

    /**
     * 设置边框的样
     *
     * @param style
     */
    public void setRectStyle(PaintStyle style) {
        mRectStyle = style;
        if (null != mElement)
            mElement.postInvalidate();
    }

    /**
     * 设置竖直的元素个数进行线绘制
     *
     * @param counts
     */
    public void setVerticalItemCounts(int counts) {
        mVerticalItemCounts = counts;
        if (null != mElement)
            mElement.postInvalidate();
    }

    /**
     * 设置水平个数
     *
     * @param counts
     */
    public void setHorizontalcounts(int counts) {
        mHorizontalCounts = counts - 1;
        if (null != mElement) {
            onCacualteLayout(mElement.getContentRect());
            mElement.postInvalidate();
        }
    }


    @Override
    void onCacualteLayout(RectF content) {
        mHeightSkip = content.height() / mHorizontalCounts;
    }

    @Override
    void onAfterDrawNodes(Canvas canvas, Zoom zoom, int startIndex, int endIndex, float max, float min) {
        if (null == mElement)
            return;
        if (null != mRectStyle) {
            mRectStyle.setPaint(mPaint);
        } else {
            mDefaultStyle.setPaint(mPaint);
        }
        canvas.drawRect(mElement.getContentRect(), mPaint);
    }

    @Override
    void onBeforeDrawNodes(Canvas canvas, Zoom zoom, int startIndex, int endIndex, float max, float min) {

        if (null == mElement)
            return;
        canvas.save();
        RectF content = mElement.getContentRect();
        if (null != mHorizontalLineStyle) {
            mHorizontalLineStyle.setPaint(mPaint);
        } else {
            mDefaultStyle.setPaint(mPaint);
        }
        int i = 1;
        final Path path = mPath;
        path.reset();
        for (; i < mHorizontalCounts; i++) {// 画水平线
            float height = content.top + i * mHeightSkip;
            path.moveTo(content.left, height);
            path.lineTo(content.right, height);
        }
        canvas.drawPath(path, mPaint);
        if (null != mVertialLineStyle) {
            mVertialLineStyle.setPaint(mPaint);
        } else {
            mDefaultStyle.setPaint(mPaint);
        }
        path.reset();
        final float screenWidth = mElement.getWidth();
        for (i = startIndex; i < endIndex; i++) {
            MaxMinNetNode node = mElement.getDrawNode(i);
            if (node.isDrawBgLine()) {
                float x = screenWidth + mElement.getXInScreen(-(i + 1) * mElement.getElementWidth());
                path.moveTo(x, content.top);
                path.lineTo(x, content.bottom);
            }
        }
        canvas.drawPath(path, mPaint);
        canvas.restore();
    }


    private PaintStyle mDefaultStyle = new PaintStyle() {

        @Override
        public void setPaint(Paint paint) {
            paint.reset();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(Color.RED);
        }
    };

    /**
     */
    public static interface MaxMinNetNode extends MaxMinElement.IMaxMinDrawNode {
        boolean isDrawBgLine();
    }
}
