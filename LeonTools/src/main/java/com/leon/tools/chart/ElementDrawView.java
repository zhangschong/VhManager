package com.leon.tools.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.leon.tools.guesture.GxqGestureDetector;

import java.util.HashSet;

/**
 * 元素绘制的主View
 *
 * @author leon.zhang
 */
public class ElementDrawView extends View {
    private RectF mDrawingRect = new RectF();
    private float mElementsScrolledX = 0f;
    private float mElementsScrolledY = 0f;
    private Scroller mScroller;

    private HashSet<IElement.ViewStatusBroswer> mStatusBrowsers = new HashSet<IElement.ViewStatusBroswer>(
            10);
    private GxqGestureDetector mGestureDetector;

    public ElementDrawView(Context context) {
        super(context);
        init();
    }

    public ElementDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ElementDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mGestureDetector = new GxqGestureDetector(getContext(), mGestureListener) {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = (event.getAction() & MotionEvent.ACTION_MASK);
                if (action == MotionEvent.ACTION_DOWN) {
                    if (!mScroller.isFinished()) {//如果滑动未完则finish
                        mScroller.abortAnimation();
                    }
                }
                return super.onTouch(v, event);
            }
        };
        mScroller = new Scroller(getContext());
        setOnTouchListener(mGestureDetector);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mInnerElementsLayout.measureLayout(left + getPaddingLeft(), top
                - getPaddingTop(), right + getPaddingRight(), bottom
                - getPaddingBottom());
    }

    /**
     * 添加元素
     *
     * @param e
     */
    public void addElements(BaseElement e) {
        mInnerElementsLayout.addElements(e);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int left = getScrollX();
        final int top = getScrollY();
        final int right = left + getWidth();
        final int bottom = top + getHeight();
        mDrawingRect.set(left + getPaddingLeft(), top + getPaddingTop(), right
                - getPaddingRight(), bottom - getPaddingTop());
        canvas.save();
        canvas.clipRect(mDrawingRect);
        mInnerElementsLayout.draw(canvas, mDrawingRect);
        canvas.restore();
    }

    /**
     * 缩放元素
     *
     * @param sx
     * @param sy
     */
    public void scaledElements(float sx, float sy) {
        for (IElement.ViewStatusBroswer b : mStatusBrowsers) {
            b.onParentScaled(sx, sy);
        }
    }

    /**
     * 移动元素
     *
     * @param x
     * @param y
     */
    public void scrolledElements(float x, float y) {
        if (mElementsScrolledX != x || mElementsScrolledY != y) {
            for (IElement.ViewStatusBroswer b : mStatusBrowsers) {
                b.onParentScrolled(mElementsScrolledX, mElementsScrolledY, x, y);
            }
            mElementsScrolledX = x;
            mElementsScrolledY = y;
        }
    }

    /**
     * 移动元素
     *
     * @param dx
     * @param dy
     */
    public final void scrolledElementsOffset(float dx, float dy) {
        scrolledElements(mElementsScrolledX + dx, mElementsScrolledY + dy);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrolledElements(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 滑动效果
     *
     * @param vx
     * @param vy
     */
    public final void flingElement(float vx, float vy) {
        final float width = getWidth();
        final float height = getHeight();
        mScroller.fling((int) mElementsScrolledX, (int) mElementsScrolledY, (int) vx, (int) vy, (int) (mElementsScrolledX - width), (int) (mElementsScrolledX + width), (int) (mElementsScrolledY - height), (int) (mElementsScrolledY + height));
        postInvalidate();
    }

    /**
     * 获取子元
     *
     * @param index
     * @return
     */
    public IElement getChildAt(int index) {
        return mInnerElementsLayout.getChildAt(index);
    }

    private BaseElementsLayout mInnerElementsLayout = new BaseElementsLayout() {

        private Runnable mRequestLayoutRunnable = new Runnable() {

            @Override
            public void run() {
                final int left = ElementDrawView.this.getLeft();
                final int top = ElementDrawView.this.getTop();
                final int right = left + ElementDrawView.this.getWidth();
                final int bottom = top + ElementDrawView.this.getHeight();
                mInnerElementsLayout.measureLayout(left + getPaddingLeft(), top
                        + getPaddingTop(), right - getPaddingRight(), bottom
                        - getPaddingBottom());
                postInvalidate();
            }
        };

        public void removeCallBack(Runnable runnable) {
            ElementDrawView.this.removeCallbacks(runnable);
        }

        public void postRunnable(Runnable runnable) {
            ElementDrawView.this.removeCallbacks(runnable);
            ElementDrawView.this.post(runnable);
        }

        /**
         * 请求重新建模
         */
        public void requestLayout() {
            postRunnable(mRequestLayoutRunnable);
        }

        @Override
        public void notifyLayoutChanged() {
            postInvalidate();
        }

        public void postInvalidate() {
            ElementDrawView.this.postInvalidate();
        }

        public void addViewStatusBroswer(ViewStatusBroswer broswer) {
            mStatusBrowsers.add(broswer);
        }

        public void removeViewStatusBroser(ViewStatusBroswer broswer) {
            mStatusBrowsers.remove(broswer);
        }

    };


    private GxqGestureDetector.SimpleOnScaleGestureListener mGestureListener = new GxqGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public void onScroll(float velocityX, float velocityY) {
            scrolledElementsOffset(velocityX, velocityY);
        }


        @Override
        public boolean onScale(GxqGestureDetector detector) {
            scaledElements(1 / detector.getScaleFactor(),
                    1.0f);
            return true;
        }


        @Override
        public void onFling(float velocityX, float velocityY) {
            flingElement(-velocityX, -velocityY);
        }
    };

}
