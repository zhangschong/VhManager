package com.leon.tools.fgmanager;

/**
 * Created by leon.zhang on 2016/6/24.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by leon.zhang on 2016/6/24.
 * VhFragment基类
 */
interface IVhFragment {
    int VH_STATE_NULL = ViewHelper.VIEW_HELPER_STATE_NULL;
    int VH_STATE_ATTACH = ViewHelper.VIEW_HELPER_STATE_ATTACH;

    /**
     * 设置当前所在FragmentManager栈中位置
     *
     * @param index
     */
    void setIndex(int index);

    /**
     * 设置当前ViewHelper
     *
     * @param vh
     */
    void setViewHelper(ViewHelper vh);

    /**
     * 获取当前所在FragmentManager栈中位置
     *
     * @return
     */
    int getIndex();

    /**
     * 获取当前的ViewHelper
     *
     * @return
     */
    @Nullable
    ViewHelper getViewHelper();

    /**
     * 获取tag Name,用于Fragment栈
     *
     * @return
     */
    @NonNull
    String getTagName();

    /**
     * 获取ViewHelper当前状态
     *
     * @return
     */
    int getVhState();


    /**
     * 是否还处于存活状态
     *
     * @return
     */
    boolean isLived();

}
