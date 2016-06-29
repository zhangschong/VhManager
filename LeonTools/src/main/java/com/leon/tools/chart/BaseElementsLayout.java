package com.leon.tools.chart;

import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Element的容
 * 
 * @author leon.zhang
 * 
 */
public class BaseElementsLayout extends BaseElement implements IElementLayout {

	private ArrayList<IElement> mElements = new ArrayList<IElement>(16);
	private LinkedList<IElement> mRequestLayoutElements = new LinkedList<IElement>();

	@Override
	public void addElements(IElement e) {
		e.create(this);
		mElements.add(e);
		requestLayout();
	}

	// }

	@Override
	public void destroy() {
		for (IElement e : mElements) {
			e.destroy();
		}
		mElements.clear();
	}

	@Override
	protected void onCreate() {
	}

	@Override
	protected void onDraw(Canvas canvas, RectF canDrawRect) {
		RectF rect = new RectF(canDrawRect);
		rect.intersect(getLocationRect());
		for (IElement e : mElements) {
			e.draw(canvas, rect);
		}
	}

	@Override
	protected void onLayoutChanged(boolean isChanged, float left, float top,
			float right, float bottom) {
		if (isChanged)
			mesureChildrenLayout();
	}

	protected void mesureChildrenLayout() {
		final int counts = mElements.size();
		RectF loaction = getLocationRect();
		float left = mPaddingRect.l;
		float top = mPaddingRect.t;
		float right = loaction.width() - mPaddingRect.r;
		float bottom = loaction.height() - mPaddingRect.b;
		for (int i = 0; i < counts; i++) {
			IElement e = mElements.get(i);
			onMesureChildLayout(e, i, counts, left, top, right, bottom);
		}
	}

	@Override
	public void notifyLayoutChanged() {
		postInvalidate();
	}

	/**
	 * 当计算子View位置时被调用
	 * 
	 * @param e
	 */
	protected void onMesureChildLayout(IElement e, int index, int counts,
			float left, float top, float right, float bottom) {
		e.measureLayout(left, top, right, bottom);
	}

	@Override
	public IElement getChildAt(int index) {
		if (index < mElements.size() && index > 0) {
			return mElements.get(index);
		}
		return null;
	}

	@Override
	public void childRequestLayout(final IElement element) {
		mRequestLayoutElements.add(element);
		postRunnable(mChildrenRequestLayoutRunnable);
	}

	private Runnable mChildrenRequestLayoutRunnable = new Runnable() {

		@Override
		public void run() {
			IElement e;
			while ((e = mRequestLayoutElements.poll()) != null) {
				mesureChild(e);
			}
		}

		void mesureChild(IElement e) {
			RectF loaction = getLocationRect();
			float left = mPaddingRect.l;
			float top = mPaddingRect.t;
			float right = loaction.width() - mPaddingRect.r;
			float bottom = loaction.height() - mPaddingRect.b;
			e.measureLayout(left, top, right, bottom);
			postInvalidate();
		}
	};

}
