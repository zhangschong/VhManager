package com.leon.tools.view;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.leon.tools.obj.ILifeObj;


/**
 * 用于控制界面刷新,通过id访问界面更快
 *
 * @author zhanghong
 */
public class UiController implements ILifeObj {
    private final static String TAG = "UIControler";

    private final View mView;
    private SparseArray<View> mViews = new SparseArray<View>();

    public UiController(View view) {
        mView = view;
    }

    public UiController(Context context, int resLayout) {
        this(View.inflate(context, resLayout, null));
    }

    /**
     * 获取当前的主View
     *
     * @return
     */
    public final View getView() {
        return mView;
    }

    @Override
    public void init() {
    }

    @Override
    public void recycle() {
        mViews.clear();
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T findViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置Text值
     *
     * @param viewId
     * @param cs
     */
    public void setTextToTextView(int viewId, CharSequence cs) {
        TextView tv = findViewById(viewId);
        if (null != tv) {
            tv.setText(cs);
        }
    }

    /**
     * 设置TextView字体色彩值
     *
     * @param viewId
     * @param color
     */
    public void setTextColorToTextView(int viewId, int color) {
        TextView tv = findViewById(viewId);
        if (null != tv) {
            tv.setTextColor(color);
        }
    }

    /**
     * 设置Text值
     *
     * @param viewId
     * @param resId
     */
    public void setTextToTextView(int viewId, int resId) {
        TextView tv = findViewById(viewId);
        if (null != tv) {
            tv.setText(resId);
        }
    }
}
