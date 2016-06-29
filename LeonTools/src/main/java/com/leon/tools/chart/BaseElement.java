package com.leon.tools.chart;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.HashSet;

public abstract class BaseElement implements IElement {

	private HashSet<ViewStatusBroswer> mStatusBrowsers = new HashSet<IElement.ViewStatusBroswer>(
			10);
	/**
	 * 位置空间
	 */
	private RectF mLocationRect = new RectF();
	protected IElementLayout mParent;
	protected InnerRect mPaddingRect = new InnerRect();

	@Override
	public RectF getLocationRect() {
		return mLocationRect;
	}

	@Override
	public void measureLayout(float left, float top, float right, float bottom) {
		layout(left, top, right, bottom);
	}

	/**
	 * 获取当前location
	 * 
	 * @return
	 */
	public final RectF getCurrentLocation() {
		return mLocationRect;
	}

	public final void layout(float left, float top, float right, float bottom) {
		RectF rect = new RectF(left, top, right, bottom);
		boolean isChanged = !mLocationRect.equals(rect);
		if (isChanged) {
			mLocationRect.set(left, top, right, bottom);
		}
		onLayoutChanged(isChanged, 0, 0, mLocationRect.width(),
				mLocationRect.height());
		postInvalidate();
	}

	/**
	 * 当layout改变时回
	 * 
	 * @param isChanged
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	protected abstract void onLayoutChanged(boolean isChanged, float left,
			float top, float right, float bottom);

	@Override
	public final void draw(Canvas canvas, RectF canDrawRect) {
		if (RectF.intersects(canDrawRect, mLocationRect)) {// 如果绘制大小在可绘制的范围内
			canvas.save();
			canvas.clipRect(mLocationRect.left, mLocationRect.top,
					mLocationRect.right, mLocationRect.bottom);
			canvas.translate(mLocationRect.left, mLocationRect.top);
			onDraw(canvas, new RectF(0,0,mLocationRect.width(),mLocationRect.height()));
			canvas.restore();
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public final void create(IElementLayout parent) {
		this.mParent = parent;
		onCreate();
		addViewStatusBroswers();
	}

	private void addViewStatusBroswers() {
		if (mStatusBrowsers.size() > 0) {
			for (ViewStatusBroswer b : mStatusBrowsers) {
				addViewStatusBroswer(b);
			}
			mStatusBrowsers.clear();
		}
	}

	@Override
	public float getWidth() {
		return mLocationRect.width();
	}

	@Override
	public float getHeight() {
		return mLocationRect.height();
	}

	/**
	 * 绘图时被调用
	 * 
	 * @param canvas
	 */
	protected abstract void onDraw(Canvas canvas, RectF canDrawRect);

	@Override
	public void notifyLayoutChanged() {
		if (null != mParent)
			mParent.notifyLayoutChanged();
	}

	@Override
	public void requestLayout() {
		if (null != mParent)
			mParent.childRequestLayout(this);
	}

	@Override
	public void postRunnable(Runnable runnable) {
		if (null != mParent)
			mParent.postRunnable(runnable);
	}

	@Override
	public void removeCallBack(Runnable runnable) {
		if (null != mParent)
			mParent.removeCallBack(runnable);
	}

	@Override
	public void addViewStatusBroswer(ViewStatusBroswer broswer) {
		if (null != mParent) {
			mParent.addViewStatusBroswer(broswer);
		} else {
			mStatusBrowsers.add(broswer);
		}
	}

	@Override
	public void removeViewStatusBroser(ViewStatusBroswer broswer) {
		if (null != mParent)
			mParent.removeViewStatusBroser(broswer);
	}

	@Override
	public void setPadding(float left, float top, float right, float bottom) {
		if (left != mPaddingRect.l || top != mPaddingRect.t
				|| right != mPaddingRect.r || bottom != mPaddingRect.b) {
			mPaddingRect.l = left;
			mPaddingRect.t = top;
			mPaddingRect.r = right;
			mPaddingRect.b = bottom;
			onPaddingChanged();
		}
	}

	protected void onPaddingChanged() {
		postInvalidate();
	}

	/**
	 * 创建时被调用
	 */
	protected abstract void onCreate();

	@Override
	public void postInvalidate() {
		if (null != mParent)
			mParent.postInvalidate();
	}

	protected class InnerRect {
		public InnerRect() {
		}

		public InnerRect(float l, float t, float r, float b) {
			this.l = l;
			this.t = t;
			this.r = r;
			this.b = b;
		}

		public float l, t, r, b;

	}

}
