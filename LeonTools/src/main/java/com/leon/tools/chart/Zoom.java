package com.leon.tools.chart;

import android.graphics.RectF;

/**
 * 用于位置平移,放缩坐标计算.
 * 
 * @author leon.zhang
 * 
 */
public final class Zoom {
	/* 实际屏幕大小 */
	private RectF mScreenRect = new RectF();
	/* 能展示的空间大小 */
	private RectF mScreenScaledRect = new RectF();
	/* 背景空间大小 */
	private RectF mRoomRect = new RectF();
	private float mCurrentScaleX = 1.0f;// 轴缩放比
	private float mCurrentScaleY = 1.0f;//
	private float mMaxScale = 2.0f;
	private float mMinScale = 0.5f;

	public Zoom() {
	}

	public Zoom(RectF screenRect) {
		mScreenRect.set(screenRect);
		mScreenScaledRect.set(screenRect);
	}

	private static float scaleValue(float scale, float value, float scalePoint) {
		return (value - scalePoint) * scale + scalePoint;
	}

	public void setScreenRect(float l, float t, float r, float b) {
		mScreenRect.set(l, t, r, b);
		mScreenScaledRect.set(l, t, r, b);
	}

	public RectF getScaledScreenRect() {
		return mScreenScaledRect;
	}

	public RectF getRoomRect() {
		return mRoomRect;
	}

	/**
	 * 设置背景空间大小
	 *
	 * @param room
	 */
	public void setRoomRect(RectF room) {
		mRoomRect.set(room);
	}

	/**
	 * 获取展示区域的宽�?
	 *
	 * @return
	 */
	public float getScreenWidth() {
		return mScreenRect.width();
	}

	/**
	 * 获取展示区域的高
	 *
	 * @return
	 */
	public float getScreenHeight() {
		return mScreenRect.height();
	}

	public RectF getScreenRect() {
		return mScreenRect;
	}

	public void setScreenRect(RectF rect) {
		mScreenRect.set(rect);
		mScreenScaledRect.set(rect);
	}

	public void setRoomRect(float l, float t, float r, float b) {
		mRoomRect.set(l, t, r, b);
		caculateScaledScreenRoomRect();
	}

	/**
	 * 设置缩放
	 *
	 * @param scale
	 */
	public void setMaxScaled(float scale) {
		mMaxScale = scale;
	}

	/**
	 * 设置缩放
	 *
	 * @param scale
	 */
	public void setMinScaled(float scale) {
		mMinScale = scale;
	}

	/*
	 * 获取当前能放缩的大小
	 *
	 * @param scale
	 *
	 * @return
	 */
	private float getRightScaled(float scale) {
		if (scale > mMaxScale) {
			return mMaxScale;
		} else if (scale < mMinScale) {
			return mMinScale;
		}
		return scale;
	}

	/**
	 * 输入x缩放比例
	 *
	 * @param x
	 */
	public void setScaledOffsetX(float x) {
		float scaleX = getRightScaled(mCurrentScaleX * x);
		if (scaleX != mCurrentScaleX) {
			mCurrentScaleX = scaleX;
			caculateScaledScreenRoomRect();
		}
	}

	/**
	 * 输入y缩放比例
	 *
	 * @param y
	 */
	public void setScaledOffsetY(float y) {
		float scaleY = getRightScaled(mCurrentScaleX * y);
		if (scaleY != mCurrentScaleY) {
			mCurrentScaleY = scaleY;
			caculateScaledScreenRoomRect();
		}
	}

	/**
	 * 平移位置
	 *
	 * @param dx
	 * @param dy
	 */
	public void offsetScreen(float dx, float dy) {
		mScreenScaledRect.offset(dx * mCurrentScaleX, dy * mCurrentScaleY);
		checkScreenRoomRect();
	}

	private void caculateScaledScreenRoomRect() {// 计算放缩数据
		final float centerX = mScreenRect.centerX();
		final float centerY = mScreenRect.centerY();
		float left = scaleValue(mCurrentScaleX, mScreenRect.left, centerX);
		float right = scaleValue(mCurrentScaleX, mScreenRect.right, centerX);
		float top = scaleValue(mCurrentScaleY, mScreenRect.top, centerY);
		float bottom = scaleValue(mCurrentScaleY, mScreenRect.bottom, centerY);
		mScreenScaledRect.set(left, top, right, bottom);
		checkScreenRoomRect();
	}

	private void checkScreenRoomRect() {
		if(mScreenRect.width() == 0 || mScreenRect.height() == 0){
			return;
		}

		float srrLeft = mScreenScaledRect.left;
		float srrRight = mScreenScaledRect.right;
		float srrTop = mScreenScaledRect.top;
		float srrBottom = mScreenScaledRect.bottom;
		float offsetX = 0f;
		float offsetY = 0f;
		if (mScreenScaledRect.width() > mRoomRect.width()) {// 计算横向数据
			srrLeft = mRoomRect.left;
			srrRight = mRoomRect.right;
		} else if (srrLeft < mRoomRect.left) {
			offsetX = mRoomRect.left - srrLeft;
		} else if (srrRight > mRoomRect.right) {
			offsetX = mRoomRect.right - srrRight;
		}

		if (mScreenScaledRect.height() > mRoomRect.height()) {// 计算纵向位置
			srrTop = mRoomRect.top;
			srrBottom = mRoomRect.bottom;
		} else if (srrTop < mRoomRect.top) {
			offsetY = mRoomRect.top - srrTop;
		} else if (srrBottom > mRoomRect.bottom) {
			offsetY = mRoomRect.bottom - srrBottom;
		}
		// 移动到当前位�?
		mScreenScaledRect.set(srrLeft, srrTop, srrRight, srrBottom);
		mScreenScaledRect.offset(offsetX, offsetY);

		mScreenRect.offset(mScreenScaledRect.centerX() - mScreenRect.centerX(),
				mScreenScaledRect.centerY() - mScreenRect.centerY());// 同步ScreenRect的位
		mCurrentScaleX = mScreenScaledRect.width() / mScreenRect.width();
		mCurrentScaleY = mScreenScaledRect.height() / mScreenRect.height();
	}

	public final float getScaledValueX(float value) {
		return value / mCurrentScaleX;
	}

	public final float getScaledValueY(float value) {
		return value / mCurrentScaleY;
	}

	/**
	 * 获取缩放后的Rect
	 *
	 * @param rect
	 * @return
	 */
	public final RectF getScaleRect(RectF rect) {
		final float centerX = mScreenRect.centerX();
		final float centerY = mScreenRect.centerY();
		float left = scaleValue(mCurrentScaleX, rect.left, centerX);
		float right = scaleValue(mCurrentScaleX, rect.right, centerX);
		float top = scaleValue(mCurrentScaleY, rect.top, centerY);
		float bottom = scaleValue(mCurrentScaleY, rect.bottom, centerY);
		return new RectF(left, top, right, bottom);
	}

}
