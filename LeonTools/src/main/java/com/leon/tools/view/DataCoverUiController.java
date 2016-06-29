package com.leon.tools.view;

import android.content.Context;
import android.view.View;

/**
 * 用于控制界面刷新
 * 
 * @author zhanghong
 * 
 */
public abstract class DataCoverUiController<ItemData> extends UiController {

	public DataCoverUiController(View view) {
		super(view);
	}

	public DataCoverUiController(Context context, int resLayout) {
		super(context, resLayout);
	}

	/**
	 * 设置修改后的值
	 * 
	 * @param data
	 */
	public final void convertData(final ItemData data) {
		onConvertData(data);
	}

	/**
	 * 设置 相关View的值
	 * 
	 * @param data
	 * @param data
	 */
	protected abstract void onConvertData(ItemData data);

}
