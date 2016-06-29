package com.leon.tools.guesture;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.leon.tools.R;


/**
 * @author twisted.ye
 *         <p/>
 *         modify by 3.24
 */
public class GxqGestureDetector implements View.OnTouchListener {
    private static final String TAG = "Level";

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return onTouchEvent(event);
    }

    /**
     * 监听手势时发生收到通知。 如果你想 监听所有不同的手势，然后实现此接口。 如果你只想要听一个子集，它可能是更容易扩展
     */
    public interface OnScaleGestureListener {

        public boolean onScale(GxqGestureDetector detector);

        public boolean onScaleBegin(GxqGestureDetector detector);

        public void onScaleEnd(GxqGestureDetector detector);

        public void onScroll(float velocityX, float velocityY);

        public void onLongPress();

        public void onFling(float velocityX, float velocityY);

    }

    /**
     * 类来扩展，当你只想要听缩放相关事件的一个子集 onScaleBegin要return true才会触发onScale
     */
    public static class SimpleOnScaleGestureListener implements
            OnScaleGestureListener {

        /**
         * 手势触发
         */
        public boolean onScale(GxqGestureDetector detector) {
            return false;
        }

        /**
         * 多点手势开始，要返回true，onScale才会被触发
         */
        public boolean onScaleBegin(GxqGestureDetector detector) {
            return true;
        }

        public void onScaleEnd(GxqGestureDetector detector) {
            // Intentionally empty
        }

        /**
         * Touch了滑动时触发。 输出x,y值 向上划动y为正，向左划时x为正 e1为划动时的Event，e2,为划动后的Event
         */
        @Override
        public void onScroll(float velocityX, float velocityY) {

        }

        /**
         * Touch了不移动一直Touch down时触发
         */
        @Override
        public void onLongPress() {

        }

        @Override
        public void onFling(float velocityX, float velocityY) {


        }
    }

    private final Context mContext;
    private final OnScaleGestureListener mListener;

    private float mFocusX;
    private float mFocusY;

    private boolean mQuickScaleEnabled;

    private float mCurrSpan;
    private float mPrevSpan;
    private float mInitialSpan;
    private float mCurrSpanX;
    private float mCurrSpanY;
    private float mPrevSpanX;
    private float mPrevSpanY;
    private long mCurrTime;
    private long mPrevTime;
    private boolean mInProgress;
    private int mSpanSlop;
    private int mMinSpan;

    // Bounds for recently seen values
    private float mTouchUpper;
    private float mTouchLower;
    private float mTouchHistoryLastAccepted;
    private int mTouchHistoryDirection;
    private long mTouchHistoryLastAcceptedTime;
    private int mTouchMinMajor;
    private MotionEvent mDoubleTapEvent;
    private int mDoubleTapMode = DOUBLE_TAP_MODE_NONE;
    private final Handler mHandler;

    private static final long TOUCH_STABILIZE_TIME = 128; // ms
    private static final int DOUBLE_TAP_MODE_NONE = 0;
    private static final int DOUBLE_TAP_MODE_IN_PROGRESS = 1;
    private static final float SCALE_FACTOR = .5f;

    private GestureDetector mGestureDetector;

    private boolean mEventBeforeOrAboveStartingGestureEvent;

    public GxqGestureDetector(Context context, OnScaleGestureListener listener) {
        this(context, listener, null);
    }

    /**
     * listener不能为空
     */
    public GxqGestureDetector(Context context, OnScaleGestureListener listener,
                              Handler handler) {
        mContext = context;
        mListener = listener;
        mSpanSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 2;

        final Resources res = context.getResources();
        mTouchMinMajor = res
                .getDimensionPixelSize(R.dimen.gxq_config_minScalingTouchMajor);
        mMinSpan = res.getDimensionPixelSize(R.dimen.gxq_config_minScalingSpan);
        mHandler = handler;
        // 仅支持4.0以上
        if (context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setQuickScaleEnabled(true);
        }
    }

    /**
     * 添加事件历史
     *
     * @param ev MotionEvent to add to the ongoing history
     */
    private void addTouchHistory(MotionEvent ev) {
        // 从开机到现在的毫秒数（手机睡眠的时间不包括在内）
        final long currentTime = SystemClock.uptimeMillis();
        final int count = ev.getPointerCount();
        boolean accept = currentTime - mTouchHistoryLastAcceptedTime >= TOUCH_STABILIZE_TIME;
        float total = 0;
        int sampleCount = 0;
        for (int i = 0; i < count; i++) {
            final boolean hasLastAccepted = !Float
                    .isNaN(mTouchHistoryLastAccepted);
            final int historySize = ev.getHistorySize();
            final int pointerSampleCount = historySize + 1;
            for (int h = 0; h < pointerSampleCount; h++) {
                float major;
                if (h < historySize) {
                    major = ev.getHistoricalTouchMajor(i, h);
                } else {
                    major = ev.getTouchMajor(i);
                }
                if (major < mTouchMinMajor)
                    major = mTouchMinMajor;
                total += major;

                if (Float.isNaN(mTouchUpper) || major > mTouchUpper) {
                    mTouchUpper = major;
                }
                if (Float.isNaN(mTouchLower) || major < mTouchLower) {
                    mTouchLower = major;
                }

                if (hasLastAccepted) {
                    final int directionSig = (int) Math.signum(major
                            - mTouchHistoryLastAccepted);
                    if (directionSig != mTouchHistoryDirection
                            || (directionSig == 0 && mTouchHistoryDirection == 0)) {
                        mTouchHistoryDirection = directionSig;
                        final long time = h < historySize ? ev
                                .getHistoricalEventTime(h) : ev.getEventTime();
                        mTouchHistoryLastAcceptedTime = time;
                        accept = false;
                    }
                }
            }
            sampleCount += pointerSampleCount;
        }

        final float avg = total / sampleCount;

        if (accept) {
            float newAccepted = (mTouchUpper + mTouchLower + avg) / 3;
            mTouchUpper = (mTouchUpper + newAccepted) / 2;
            mTouchLower = (mTouchLower + newAccepted) / 2;
            mTouchHistoryLastAccepted = newAccepted;
            mTouchHistoryDirection = 0;
            mTouchHistoryLastAcceptedTime = ev.getEventTime();
        }
    }

    /**
     * 清除历史事件
     */
    private void clearTouchHistory() {
        mTouchUpper = Float.NaN;
        mTouchLower = Float.NaN;
        mTouchHistoryLastAccepted = Float.NaN;
        mTouchHistoryDirection = 0;
        mTouchHistoryLastAcceptedTime = 0;
    }

    public boolean onTouchEvent(MotionEvent event) {

        mCurrTime = event.getEventTime();

        final int action = event.getActionMasked();

        // 转发事件
        if (mQuickScaleEnabled) {
            mGestureDetector.onTouchEvent(event);
        }

        final boolean streamComplete = action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL;

        if (action == MotionEvent.ACTION_DOWN || streamComplete) {
            // 重置任何规模的监听器的进度。
            // 如果它是一个ACTION_DOWN我们开始一个新的事件流。
            // 这意味着应用程序可能并没有给我们所有的事件。
            if (mInProgress) {
                mListener.onScaleEnd(this);
                mInProgress = false;
                mInitialSpan = 0;
                mDoubleTapMode = DOUBLE_TAP_MODE_NONE;
            } else if (mDoubleTapMode == DOUBLE_TAP_MODE_IN_PROGRESS
                    && streamComplete) {
                mInProgress = false;
                mInitialSpan = 0;
                mDoubleTapMode = DOUBLE_TAP_MODE_NONE;
            }

            if (streamComplete) {
                clearTouchHistory();
                return true;
            }
        }

        final boolean configChanged = action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_POINTER_UP
                || action == MotionEvent.ACTION_POINTER_DOWN;

        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? event.getActionIndex() : -1;

        // 确定一个点
        float sumX = 0, sumY = 0;
        final int count = event.getPointerCount();
        final int div = pointerUp ? count - 1 : count;
        final float focusX;
        final float focusY;
        if (mDoubleTapMode == DOUBLE_TAP_MODE_IN_PROGRESS) {
            // 在双击模式下，焦点始终是其中双击
            // 手势开始
            focusX = mDoubleTapEvent.getX();
            focusY = mDoubleTapEvent.getY();
            if (event.getY() < focusY) {
                mEventBeforeOrAboveStartingGestureEvent = true;
            } else {
                mEventBeforeOrAboveStartingGestureEvent = false;
            }
        } else {
            for (int i = 0; i < count; i++) {
                if (skipIndex == i)
                    continue;
                sumX += event.getX(i);
                sumY += event.getY(i);
            }

            focusX = sumX / div;
            focusY = sumY / div;
        }

        addTouchHistory(event);

        // 确定来自焦点平均偏差
        float devSumX = 0, devSumY = 0;
        for (int i = 0; i < count; i++) {
            if (skipIndex == i)
                continue;

            // 转换得到的直径为半径
            final float touchSize = mTouchHistoryLastAccepted / 2;
            devSumX += Math.abs(event.getX(i) - focusX) + touchSize;
            devSumY += Math.abs(event.getY(i) - focusY) + touchSize;
        }
        final float devX = devSumX / div;
        final float devY = devSumY / div;

        // 跨度是通过聚焦点触摸点之间的平均距离;I.S.与来自焦点的平均偏差的半径的圆的直径。
        final float spanX = devX * 2;
        final float spanY = devY * 2;
        final float span;
        if (inDoubleTapMode()) {
            span = spanY;
        } else {
            span = FloatMath.sqrt(spanX * spanX + spanY * spanY);
        }

        // 根据需要派遣开始/结束事件。
        // 如果配置更改，通知应用程序重置其当前
        // 通过状态开始
        // 一个新的事件流。
        final boolean wasInProgress = mInProgress;
        mFocusX = focusX;
        mFocusY = focusY;
        if (!inDoubleTapMode() && mInProgress
                && (span < mMinSpan || configChanged)) {
            mListener.onScaleEnd(this);
            mInProgress = false;
            mInitialSpan = span;
            mDoubleTapMode = DOUBLE_TAP_MODE_NONE;
        }
        if (configChanged) {
            mPrevSpanX = mCurrSpanX = spanX;
            mPrevSpanY = mCurrSpanY = spanY;
            mInitialSpan = mPrevSpan = mCurrSpan = span;
        }

        final int minSpan = inDoubleTapMode() ? mSpanSlop : mMinSpan;
        if (!mInProgress && span >= minSpan
                && (wasInProgress || Math.abs(span - mInitialSpan) > mSpanSlop)) {
            mPrevSpanX = mCurrSpanX = spanX;
            mPrevSpanY = mCurrSpanY = spanY;
            mPrevSpan = mCurrSpan = span;
            mPrevTime = mCurrTime;
            mInProgress = mListener.onScaleBegin(this);
        }

        // 焦点和跨度/比例因子正在发生变化。
        if (action == MotionEvent.ACTION_MOVE) {
            mCurrSpanX = spanX;
            mCurrSpanY = spanY;
            mCurrSpan = span;

            boolean updatePrev = true;

            if (mInProgress) {
                updatePrev = mListener.onScale(this);
            }

            if (updatePrev) {
                mPrevSpanX = mCurrSpanX;
                mPrevSpanY = mCurrSpanY;
                mPrevSpan = mCurrSpan;
                mPrevTime = mCurrTime;
            }
        }

        return true;
    }

    private boolean inDoubleTapMode() {
        return mDoubleTapMode == DOUBLE_TAP_MODE_IN_PROGRESS;
    }

    /**
     * 初始化监听除多点监控外的事件
     */
    public void setQuickScaleEnabled(boolean scales) {
        mQuickScaleEnabled = scales;
        if (mQuickScaleEnabled && mGestureDetector == null) {
            GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    /**
                     * 双击的第二下Touch down时触发
                     */
                    mDoubleTapEvent = e;
                    mDoubleTapMode = DOUBLE_TAP_MODE_IN_PROGRESS;
                    return true;
                }


                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2,
                                       float velocityX, float velocityY) {
                    mListener.onFling(velocityX, velocityY);
                    return super.onFling(e1, e2, velocityX, velocityY);
                }


                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                        float distanceX, float distanceY) {
                    mListener.onScroll(distanceX, distanceY);
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    mListener.onLongPress();
                    super.onLongPress(e);
                }

            };
            mGestureDetector = new GestureDetector(mContext, gestureListener,
                    mHandler);
        }
    }

    /**
     * 设置是否监听除多点监控外的事件
     */
    public boolean isQuickScaleEnabled() {
        return mQuickScaleEnabled;
    }

    /**
     * 如果手势处于进行过程中，返回true.
     */
    public boolean isInProgress() {
        return mInProgress;
    }

    /**
     * 返回当前手势焦点的X坐标。 如果手势正在进行中，焦点位于组成手势的两个触点之间。
     * 如果手势正在结束，焦点为仍留在屏幕上的触点的位置。若isInProgress（）返回false，该方法的返回值未定义。 　　 返回值
     * 返回焦点的X坐标值，以像素为单位。
     */
    public float getFocusX() {
        return mFocusX;
    }

    /**
     * 返回当前手势焦点的Y坐标。 如果手势正在进行中，焦点位于组成手势的两个触点之间。
     * 如果手势正在结束，焦点为仍留在屏幕上的触点的位置。若isInProgress（）返回false，该方法的返回值未定义。 　　 返回值
     * 返回焦点的Y坐标值，以像素为单位。
     */
    public float getFocusY() {
        return mFocusY;
    }

    /**
     * 返回手势过程中，组成该手势的两个触点的当前距离。
     *
     * @return 以像素为单位的距离。
     */
    public float getCurrentSpan() {
        return mCurrSpan;
    }

    /**
     * 返回每个通过协调形成进步的姿态指针之间的平均距离X。
     *
     * @return 以像素为单位的距离.
     */
    public float getCurrentSpanX() {
        return mCurrSpanX;
    }

    /**
     * 返回每个通过协调形成进步的姿态指针之间的平均距离Y。
     *
     * @return 以像素为单位的距离.
     */
    public float getCurrentSpanY() {
        return mCurrSpanY;
    }

    /**
     * 返回手势过程中，组成该手势的两个触点的前一次距离。
     *
     * @return 两点的前一次距离，以像素为单位.
     */
    public float getPreviousSpan() {
        return mPrevSpan;
    }

    /**
     * 返回每个通过协调形成以前两手指之间的平均距离X。
     *
     * @return 以像素为单位的距离.
     */
    public float getPreviousSpanX() {
        return mPrevSpanX;
    }

    /**
     * 返回每个通过协调形成以前两手指之间的平均距离Y。
     *
     * @return 以像素为单位的距离.
     */
    public float getPreviousSpanY() {
        return mPrevSpanY;
    }

    /**
     * 返回从前一个伸缩事件至当前伸缩事件的伸缩比率。该值定义为 (getCurrentSpan() / getPreviousSpan())。
     *
     * @return 当前缩放比例。
     */
    public float getScaleFactor() {
        if (inDoubleTapMode()) {
            final boolean scaleUp = (mEventBeforeOrAboveStartingGestureEvent && (mCurrSpan < mPrevSpan))
                    || (!mEventBeforeOrAboveStartingGestureEvent && (mCurrSpan > mPrevSpan));
            final float spanDiff = (Math.abs(1 - (mCurrSpan / mPrevSpan)) * SCALE_FACTOR);
            return mPrevSpan <= 0 ? 1 : scaleUp ? (1 + spanDiff)
                    : (1 - spanDiff);
        }
        return mPrevSpan > 0 ? mCurrSpan / mPrevSpan : 1;
    }

    /**
     * 返回在先前接受缩放事件和当前缩放事件之间毫秒的时间差。
     *
     * @return 因为以毫秒为单位的最后缩放事件的时间差。
     */
    public long getTimeDelta() {
        return mCurrTime - mPrevTime;
    }

    /**
     * 返回当前事件的事件时间被处理。
     *
     * @return 当前事件时间（毫秒）。
     */
    public long getEventTime() {
        return mCurrTime;
    }
}
