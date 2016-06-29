package com.leon.tools.chart;

/**
 * 容器接口
 * 
 * @author leon.zhang
 * 
 */
public interface IElementLayout extends IElement {
	/**
	 * 添加元素
	 * 
	 * @param e
	 */
	void addElements(IElement e);

	// /**
	// * 添加重绘区域
	// *
	// * @param rect
	// */
	// void addDirtyRect(RectF rect);

	/**
	 * 获取子元
	 * 
	 * @param index
	 * @return
	 */
	IElement getChildAt(int index);

	/**
	 * 请求重新计算位置
	 * 
	 * @param element
	 */
	void childRequestLayout(IElement element);

}
