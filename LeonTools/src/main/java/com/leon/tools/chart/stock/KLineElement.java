package com.leon.tools.chart.stock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.leon.tools.chart.BaseElement;
import com.leon.tools.chart.IElement;
import com.leon.tools.chart.PaintStyle;
import com.leon.tools.chart.Zoom;

import java.util.ArrayList;

/**
 * Created by kutear.guo on 2016/3/25.
 */
public class KLineElement extends BaseElement implements IElement.ViewStatusBroswer {

    private static final int OFFSET_SCREEN_POINTS = 5;
    private float mMax = 1000;
    private float mMin = 0;
    private RectF mScreenArea = new RectF();  //对应显示区域
    private Zoom mZoom = new Zoom();
    private float mUnitWidth = 20;   //绘制间隔
    private ArrayList<ArrayList<Float>> mLine = new ArrayList<>();
    private PaintStyle.LineStyle[] mColors;
    private Runnable mCalculateRunnable = new Runnable() {
        @Override
        public void run() {
            calculateMaxAndMin();
            if (mUnitWidth * getLinesPointsCount() < mScreenArea.width()) {
                mUnitWidth = mScreenArea.width() / (getLinesPointsCount() - 1);
            }
            mZoom.setRoomRect(-((getLinesPointsCount() - 1) * mUnitWidth),
                    mScreenArea.top, 0, mScreenArea.bottom);
            mZoom.setScreenRect(-mScreenArea.width(), mScreenArea.top, 0, mScreenArea.bottom);
            postInvalidate();
        }
    };

    public void setUnitWidth(float width) {
        this.mUnitWidth = width;
        postRunnable(mCalculateRunnable);
    }

    /**
     * 获取线的点的数量
     *
     * @return
     */
    private float getLinesPointsCount() {
        return (mLine != null && mLine.size() > 0) ? mLine.get(0).size() : 0;
    }

    /**
     * 根据现有数据计算极
     */
    private void calculateMaxAndMin() {
        mMin = Float.MAX_VALUE;
        mMax = Float.MIN_VALUE;
        for (ArrayList<Float> line : mLine) {
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

    public void setColor(PaintStyle.LineStyle... color) {
        this.mColors = color;
        postInvalidate();
    }

    public void setUnitWidth(int width) {
        this.mUnitWidth = width;
        postRunnable(mCalculateRunnable);
    }

    public void setLines(ArrayList<ArrayList<Float>> datas) {
        this.mLine = datas;
        postRunnable(mCalculateRunnable);
    }

    @Override
    protected void onLayoutChanged(boolean isChanged, float left, float top, float right, float bottom) {
        setContentRect(left + mPaddingRect.l, top + mPaddingRect.t, right
                - mPaddingRect.r, bottom - mPaddingRect.b);
    }

    protected final void setContentRect(float left, float top, float right,
                                        float bottom) {
        mScreenArea.set(left, top, right, bottom);
        postRunnable(mCalculateRunnable);
    }

    private int getStartDrawIndex() {
        return (int) Math.max(0, mZoom.getScaledScreenRect().right / mUnitWidth - OFFSET_SCREEN_POINTS);
    }

    private int getEndDrawIndex() {
        return (int) Math.min(getLinesPointsCount() - 1, Math.abs(mZoom.getScaledScreenRect().left) / mUnitWidth + OFFSET_SCREEN_POINTS);
    }

    @Override
    protected void onDraw(Canvas canvas, RectF canDrawRect) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        canvas.save();
        canvas.clipRect(mScreenArea);
        canvas.translate(mScreenArea.right, mScreenArea.bottom);
        for (int i = 0; i < mLine.size(); i++) {
            drawLine(canvas, i, paint);
        }
        canvas.restore();
    }

    private void drawLine(Canvas canvas, int lineIndex, Paint paint) {
        PaintStyle.LineStyle style = mColors != null && mColors.length > lineIndex ? mColors[lineIndex] : new PaintStyle.LineStyle(Color.GRAY, 2);
        style.setPaint(paint);
        for (int i = getStartDrawIndex(); i < getEndDrawIndex(); i++) {
            canvas.drawLine(getScreenX(-mUnitWidth * i), getScreenY(mLine.get(lineIndex).get(i)),
                    getScreenX(-mUnitWidth * (i + 1)), getScreenY(mLine.get(lineIndex).get(i + 1)), paint);
        }
    }

    private float getScreenX(float readlyX) {
        return mZoom.getScaledValueX(readlyX - mZoom.getScreenRect().right);
    }

    private float getScreenY(float valueY) {
        return -mScreenArea.height() * (valueY - mMin) / (mMax - mMin);
    }

    @Override
    protected void onCreate() {
        addViewStatusBroswer(this);
    }

    @Override
    public void onParentScrolled(float lx, float ly, float cx, float cy) {
        mZoom.offsetScreen(cx - lx, cy - ly);
        postInvalidate();
    }

    @Override
    public void onParentScaled(float scaleX, float scaleY) {
        mZoom.setScaledOffsetX(scaleX);
        postInvalidate();
    }
}
