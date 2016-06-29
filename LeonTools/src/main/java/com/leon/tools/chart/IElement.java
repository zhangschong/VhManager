package com.leon.tools.chart;

import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * 绘制元素
 * 
 * @author leon.zhang
 * 
 */
public interface IElement {

	/**
	 *
	 * @param parent
	 */
	void create(IElementLayout parent);

	/**
	 *
	 * @param canvas
	 */
	void draw(Canvas canvas, RectF canDrawRect);

	/**
	 *
	 */
	RectF getLocationRect();

	/**
	 */
	void destroy();

	/**
	 *
	 * @return
	 */
	float getWidth();

	/**
	 *
	 * @return
	 */
	float getHeight();

	/**
	 */
	void postInvalidate();

	/**
	 */
	void requestLayout();

	/**
	 */
	void notifyLayoutChanged();

	/**
	 *
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	void setPadding(float left, float top, float right, float bottom);

	/**
	 *
	 * @param runnable
	 */
	void removeCallBack(Runnable runnable);

	/**
	 *
	 */
	void postRunnable(Runnable runnable);

	/**
	 *
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	void measureLayout(float left, float top, float right, float bottom);

	/**
	 *
	 * @param broswer
	 */
	void addViewStatusBroswer(ViewStatusBroswer broswer);

	/**
	 *
	 * @param broswer
	 */
	void removeViewStatusBroser(ViewStatusBroswer broswer);

	/**
	 *
	 * @author leon.zhang
	 * 
	 */
	public static interface ViewStatusBroswer {
		/**
		 *
		 * @param lx
		 * @param ly
		 * @param cx
		 * @param cy
		 */
		void onParentScrolled(float lx, float ly, float cx, float cy);

		/**
		 *
		 */
		void onParentScaled(float scaleX, float scaleY);
	}

}
