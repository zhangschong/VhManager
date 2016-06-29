package com.leon.tools.chart.stock;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.leon.tools.chart.IElement;
import com.leon.tools.chart.PercentElement;
import com.leon.tools.chart.Zoom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Layout绘制
 *
 * @param <E>
 * @author leon.zhang
 */
public class MaxMinElement<E extends MaxMinElement.IMaxMinDrawNode> extends
        PercentElement implements IElement.ViewStatusBroswer {

    protected Zoom mZoom = new Zoom();
    protected RectF mContentRect = new RectF();
    protected float mShowElementCounts = 10f;
    protected float mElementWidth;
    protected ArrayList<E> mElements = new ArrayList<E>();
    protected float mCurrentMax, mCurrentMin;
    protected int mLastStartIndex, mLastEndIndex;
    private HashSet<MaxMinObserver> mMaxMinObservers = new HashSet<MaxMinObserver>();
    private MaxMinElementBg<E> maxMinElementBg;

    @Override
    protected void onDraw(Canvas canvas, RectF canDrawRect) {
        canvas.save();
        canvas.clipRect(mContentRect);
        // 将范围控制在二项�?x:(-,0] y:(-,0];
        final int start = getStartIndex();
        final int end = getEndIndex();
        if (start != mLastStartIndex || end != mLastEndIndex) {
            caculateMaxMinInBox(start, end);
            mLastStartIndex = start;
            mLastEndIndex = end;
        }
        if (null != maxMinElementBg) {
            maxMinElementBg.onBeforeDrawNodes(canvas, mZoom, mLastStartIndex, mLastEndIndex, mCurrentMax, mCurrentMin);
        }
        canvas.save();
        canvas.translate(mContentRect.right, mContentRect.bottom);
        drawElements(canvas, start, end);
        canvas.restore();
        if (null != maxMinElementBg) {
            maxMinElementBg.onAfterDrawNodes(canvas, mZoom, mLastStartIndex, mLastEndIndex, mCurrentMax, mCurrentMin);
        }
        canvas.restore();
    }

    public float getElementWidth() {
        return mElementWidth;
    }

    private int getStartIndex() {
        int index = getItemIndexByPx(mZoom.getScaledScreenRect().right
                - mContentRect.right, -1f);
        return Math.max(index, 0);
    }

    private int getEndIndex() {
        int index = getItemIndexByPx(mZoom.getScaledScreenRect().left
                - mContentRect.right, 1f);
        return Math.min(index, mElements.size() - 1);
    }

    /**
     * 获取x(相于对整体背的相对于绘制屏幕的位
     *
     * @param x
     * @return
     */
    protected final float getXInScreen(float x) {
        return mZoom.getScaledValueX(x + mContentRect.right
                - mZoom.getScaledScreenRect().right);
    }

    private int getItemIndexByPx(float px, float addIndex) {
        return (int) (Math.abs(px) / mElementWidth + addIndex);
    }

    private void drawElements(Canvas canvas, int start, int end) {
        final float eWidthInScreen = mZoom.getScaledValueX(mElementWidth);
        for (int i = start; i <= end; i++) {
            canvas.save();
            canvas.translate(getXInScreen(-i * mElementWidth), 0);
            mElements.get(i).drawElement(canvas, i, mCurrentMax, mCurrentMin,
                    eWidthInScreen, mContentRect.height());
            canvas.restore();
        }
    }

    /**
     * 通过位置获取绘制
     *
     * @param index
     * @return
     */
    public final E getDrawNode(int index) {
        return mElements.get(index);
    }

    private void caculateMaxMinInBox(int startIndex, int endIndex) {
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (int i = startIndex; i <= endIndex; i++) {
            IMaxMinDrawNode e = mElements.get(i);
            max = Math.max(max, e.getMax());
            min = Math.min(min, e.getMin());
        }
        if (mCurrentMax != max || mCurrentMin != min) {
            mCurrentMax = max;
            mCurrentMin = min;
            if (mMaxMinObservers.size() > 0) {
                for (MaxMinObserver b : mMaxMinObservers) {
                    b.onMaxMinChanged(this, max, min);
                }
            }
        }
    }

    /**
     * 添加观察
     *
     * @param observer
     */
    public final void addMaxMinObserver(MaxMinObserver observer) {
        mMaxMinObservers.add(observer);
    }

    /**
     * 移除观察
     *
     * @param observer
     */
    public final void removeMaxMinObserver(MaxMinObserver observer) {
        mMaxMinObservers.remove(observer);
    }

    /**
     * 提示部份数据刷新
     *
     * @param node
     */
    public final void notifyDataUpdate(E node) {
        int index = mElements.indexOf(node);
        if (index >= mLastStartIndex || index <= mLastEndIndex) {
            postInvalidate();
        }
        if (mElements.get(index) != node) {
            mElements.remove(index);
            mElements.add(index, node);
        }
    }

    @Override
    protected void onCreate() {
        addViewStatusBroswer(this);
    }

    @Override
    public void onParentScaled(float scaleX, float scaleY) {
        mZoom.setScaledOffsetX(scaleX);
        postInvalidate();
    }

    @Override
    public void onParentScrolled(float lx, float ly, float cx, float cy) {
        mZoom.offsetScreen(cx - lx, cy - ly);
        postInvalidate();
    }

    /**
     * 设置IElement数组
     */
    public void setElementDatas(List<E> elements) {
        mElements.clear();
        mElements.addAll(elements);
        postRunnable(mCaculateRunnable);
    }

    /**
     * 添加数据
     *
     * @param elements
     */
    public void addElementDatas(List<E> elements) {
        mElements.addAll(elements);
        postRunnable(mCaculateRunnable);
    }

    @Override
    protected void onLayoutChanged(boolean isChanged, float left, float top,
                                   float right, float bottom) {
        setContentRect(left + mPaddingRect.l, top + mPaddingRect.t, right
                - mPaddingRect.r, bottom - mPaddingRect.b);
    }

    /**
     * 设置展示内容的位
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    protected final void setContentRect(float left, float top, float right,
                                        float bottom) {
        mContentRect.set(left, top, right, bottom);
        mZoom.setScreenRect(left, top, right, bottom);
        postRunnable(mCaculateRunnable);
    }

    public final RectF getContentRect() {
        return mContentRect;
    }


    private Runnable mCaculateRunnable = new Runnable() {

        @Override
        public void run() {
            mElementWidth = mContentRect.width() / mShowElementCounts;
            final float layoutWidth = mElements.size() * mElementWidth;
            mZoom.setRoomRect(mContentRect.right - layoutWidth,
                    mContentRect.top, mContentRect.right, mContentRect.bottom);
            if (null != maxMinElementBg) {
                maxMinElementBg.onCacualteLayout(mContentRect);
            }
            postInvalidate();
        }
    };

    /**
     * 设置背景
     *
     * @param bg
     */
    public final void setMaxMinElementBg(MaxMinElementBg bg) {
        maxMinElementBg = bg;
        if (null != maxMinElementBg) {
            maxMinElementBg.setMaxMinElement(this);
        }
        postRunnable(mCaculateRunnable);
    }

    /**
     * 设置上与下的位置
     *
     * @param tPercent
     * @param bPercent
     */
    public void setLayoutPercent(float tPercent, float bPercent) {
        setLayoutPercent(0, tPercent, 1, bPercent);
    }

    /**
     * 绘制的单
     *
     * @author leon.zhang
     */
    public static interface IMaxMinDrawNode {
        /**
         * 获取
         *
         * @return
         */
        float getMax();

        /**
         * 绘制当前元素
         *
         * @param canvas
         * @param max
         * @param min
         * @param maxWidth
         * @param maxHeight
         */
        void drawElement(Canvas canvas, int index, float max, float min, float maxWidth,
                         float maxHeight);

        /**
         * 获取
         *
         * @return
         */
        float getMin();
    }

    /**
     * MaxMin监听
     *
     * @author leon.zhang
     */
    public static interface MaxMinObserver {
        /**
         * 当最大和改变时回
         *
         * @param element
         * @param max
         * @param min
         */
        void onMaxMinChanged(MaxMinElement<?> element, float max, float min);
    }

}

/**
 * 用于绘制背景
 */
abstract class MaxMinElementBg<E extends MaxMinElement.IMaxMinDrawNode> {
    protected MaxMinElement<E> mElement;

    final void setMaxMinElement(MaxMinElement<E> element) {
        mElement = element;
    }

    /**
     * 当计算出当前元素内容大小时调
     *
     * @param content
     */
    abstract void onCacualteLayout(RectF content);

    /**
     * 在绘制元素前调用
     *
     * @param zoom
     * @param startIndex
     * @param endIndex
     * @param max
     * @param min
     */
    abstract void onBeforeDrawNodes(Canvas canvas, Zoom zoom, int startIndex, int endIndex, float max, float min);

    /**
     * 绘制元素后调
     *
     * @param canvas
     * @param zoom
     * @param startIndex
     * @param endIndex
     * @param max
     * @param min
     */
    abstract void onAfterDrawNodes(Canvas canvas, Zoom zoom, int startIndex, int endIndex, float max, float min);
}
