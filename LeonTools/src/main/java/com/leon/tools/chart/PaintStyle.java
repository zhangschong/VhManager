package com.leon.tools.chart;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;

/**
 * 设置Paint的样
 * 
 * @author leon.zhang
 * 
 */
public interface PaintStyle {

	void setPaint(Paint paint);

	/**
	 * 线条风格
	 * 
	 * @author leon.zhang
	 * 
	 */
	public static class LineStyle implements PaintStyle {

		private int lineColor;
		private float stokenWidth;

		/**
		 * 
		 * @param color
		 *            色彩
		 * @param stokenWidth
		 *            线条宽度
		 */
		public LineStyle(int color, float stokenWidth) {
			lineColor = color;
			this.stokenWidth = stokenWidth;
		}

		@Override
		public void setPaint(Paint paint) {
			paint.reset();
			paint.setStyle(Style.STROKE);
			paint.setColor(lineColor);
			paint.setStrokeWidth(stokenWidth);
		}

	}

	/**
	 * DashLine style
	 * 
	 * @author leon.zhang
	 * 
	 */
	public static class DashLineStyle extends LineStyle {
		// private float[] intervals;
		// private float offset;
		private DashPathEffect mDashEffect;

		/**
		 * 
		 * @param color
		 * @param stokenWidth
		 * @param intervals
		 * 
		 * @see DashLineStyle(int, float ,float[], float)
		 */
		public DashLineStyle(int color, float stokenWidth, float[] intervals) {
			this(color, stokenWidth, intervals, 1);
		}

		/**
		 * 
		 * @param color
		 *            色彩
		 * @param stokenWidth
		 *            线条宽度
		 * @param intervals
		 *            dash (length = 2*n (n > 0))
		 */
		public DashLineStyle(int color, float stokenWidth, float[] intervals,
				float phase) {
			super(color, stokenWidth);
			mDashEffect = new DashPathEffect(intervals, phase);
		}

		@Override
		public void setPaint(Paint paint) {
			super.setPaint(paint);
			paint.setPathEffect(mDashEffect);
		}

	}

}
