package com.leon.tools.chart;

import android.graphics.RectF;

/**
 * 百分比布
 * 
 * @author leon.zhang
 * 
 */
public class PercentElementLayout extends BaseElementsLayout {
	private InnerRect mPercentRect = new InnerRect(0, 0, 1, 1);

	@Override
	public final void measureLayout(float left, float top, float right,
			float bottom) {
		RectF rect = caculateLocationLayout(left, top, right, bottom);
		layout(rect.left, rect.top, rect.right, rect.bottom);
	}

	/**
	 * 计算位置
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @return
	 */
	private final RectF caculateLocationLayout(float left, float top,
			float right, float bottom) {
		final float layoutHeight = bottom - top;
		final float layoutWidth = right - left;
		final float l = left + layoutWidth * mPercentRect.l;
		final float r = left + layoutWidth * mPercentRect.r;
		final float t = top + layoutHeight * mPercentRect.t;
		final float b = top + layoutHeight * mPercentRect.b;
		return new RectF(l, t, r, b);
	}

	/**
	 * 设置对于父布百分比方
	 * 
	 * @param l
	 * @param t
	 * @param r
	 * @param b
	 */
	public final void setLayoutPercent(float l, float t, float r, float b) {
		mPercentRect.l = l;
		mPercentRect.t = t;
		mPercentRect.r = r;
		mPercentRect.b = b;
		requestLayout();
	}

}
