package com.leon.tools.chart.stock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;

import com.leon.tools.chart.PaintStyle;
import com.leon.tools.chart.PercentElement;


/**
 * 固定网格Element
 * 
 * @author leon.zhang
 * 
 */
public class GridElements extends PercentElement {

	private Paint mPaint = new Paint();

	private int mHorizontalCounts = 4;
	private int mVerticalCounts = 4;

	private PaintStyle mHorizontalLineStyle;
	private PaintStyle mVertialLineStyle;
	private PaintStyle mRectStyle;
	private float mWidthSkip, mHeightSkip;
	private RectF mContentRect = new RectF();
	private Path mPath = new Path();

	@Override
	protected void onDraw(Canvas canvas, RectF canDrawRect) {
		if (null != mHorizontalLineStyle) {
			mHorizontalLineStyle.setPaint(mPaint);
		} else {
			mDefaultStyle.setPaint(mPaint);
		}
		int i = 1;
		final Path path = mPath;
		path.reset();
		for (; i < mHorizontalCounts; i++) {// 画水平线
			float height = mContentRect.top + i * mHeightSkip;
			path.moveTo(mContentRect.left, height);
			path.lineTo(mContentRect.right, height);
		}
		canvas.drawPath(path, mPaint);
		if (null != mVertialLineStyle) {
			mVertialLineStyle.setPaint(mPaint);
		} else {
			mDefaultStyle.setPaint(mPaint);
		}
		i = 1;
		path.reset();
		for (; i < mVerticalCounts; i++) {
			float width = mContentRect.left + i * mWidthSkip;
			path.moveTo(width, mContentRect.top);
			path.lineTo(width, mContentRect.bottom);
		}
		canvas.drawPath(path, mPaint);

		if (null != mRectStyle) {
			mRectStyle.setPaint(mPaint);
		} else {
			mDefaultStyle.setPaint(mPaint);
		}
		canvas.drawRect(mContentRect, mPaint);
	}

	@Override
	protected void onCreate() {

	}

	/**
	 * 设置竖直的个
	 * 
	 * @param counts
	 */
	public void setVerticalCounts(int counts) {
		mVerticalCounts = counts - 1;
		postRunnable(mCaculateRunnable);
	}

	/**
	 * 设置水平个数
	 * 
	 * @param counts
	 */
	public void setHorizontalcounts(int counts) {
		mHorizontalCounts = counts - 1;
		postRunnable(mCaculateRunnable);
	}

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
		postInvalidate();
	}

	/**
	 * 设置边框的样
	 * 
	 * @param style
	 */
	public void setRectStyle(PaintStyle style) {
		mRectStyle = style;
		postInvalidate();
	}

	@Override
	protected void onLayoutChanged(boolean isChanged, float left, float top,
			float right, float bottom) {
		if (isChanged) {
			postRunnable(mCaculateRunnable);
		}
	}

	@Override
	protected void onPaddingChanged() {
		postRunnable(mCaculateRunnable);
	}

	private Runnable mCaculateRunnable = new Runnable() {

		@Override
		public void run() {
			mContentRect.set(mPaddingRect.l, mPaddingRect.t, getWidth()
					- mPaddingRect.r, getHeight() - mPaddingRect.b);
			mWidthSkip = mContentRect.width() / mVerticalCounts;
			mHeightSkip = mContentRect.height() / mHorizontalCounts;
			postInvalidate();
		}
	};

	private PaintStyle mDefaultStyle = new PaintStyle() {

		@Override
		public void setPaint(Paint paint) {
			paint.reset();
			paint.setStyle(Style.STROKE);
			paint.setStrokeWidth(1f);
			paint.setColor(Color.RED);
		}
	};

}
