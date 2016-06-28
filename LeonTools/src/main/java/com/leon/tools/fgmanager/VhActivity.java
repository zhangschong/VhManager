package com.leon.tools.fgmanager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by leon.zhang on 2016/6/24.
 */
public abstract class VhActivity extends FragmentActivity {
    private ViewGroup mLayoutImpl;
    private IVHManager mVhManager;

    /**
     * 生成ViewHelperManager实例对象
     *
     * @param layoutId
     * @return
     */
    protected IVHManager newInstanceVhManager(int layoutId) {
        return new VhManagerImpl(this, getFragmentManager(), new Handler(), layoutId);
    }

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        mLayoutImpl = new FrameLayout(this);
        mLayoutImpl.setId(com.leon.tools.R.id.main_layout_id);
        mVhManager = newInstanceVhManager(mLayoutImpl.getId());
        mVhManager.init();
        super.onCreate(savedInstanceState);
        super.setContentView(mLayoutImpl);
        if (null == savedInstanceState)
            onActivityCreated();
        else
            onActivityRestored(savedInstanceState);
    }

    /**
     * 当Activity重建时调用
     *
     * @param savedInstanceState 保存的临时数据,不可能为NULL
     */
    protected void onActivityRestored(Bundle savedInstanceState) {
    }

    /**
     * 当Activity创建时调用
     */
    protected abstract void onActivityCreated();

    @Override
    public void onBackPressed() {
        if (!mVhManager.back()) {
            super.onBackPressed();
        }
    }

    /**
     * 获取当前的ViewHelper manager
     *
     * @return
     */
    public IVHManager getViewHelperManager() {
        return mVhManager;
    }

    @Override
    protected void onDestroy() {
        mVhManager.recycle();
        super.onDestroy();
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mLayoutImpl.addView(view, params);
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup.inflate(this, layoutResID, mLayoutImpl);
    }

    /**
     * 获取当前ViewHelper manager
     *
     * @return
     */
    public final IVHManager getVhManager() {
        return mVhManager;
    }

    /**
     * 启动一个ViewHelper
     *
     * @param vhcls
     * @see #startViewHelper(Class, Message)
     * @see #startViewHelper(Class, int, Message)
     * @see #startViewHelper(Class, int)
     */
    public void startViewHelper(Class<? extends ViewHelper> vhcls) {
        mVhManager.startViewHelper(vhcls);
    }

    /**
     * 启动一个ViewHelper,并传入跳转参数
     *
     * @param vhcls
     * @param msg
     * @see #startViewHelper(Class)
     * @see #startViewHelper(Class, int, Message)
     * @see #startViewHelper(Class, int)
     */
    public void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg) {
        mVhManager.startViewHelper(vhcls, msg);
    }

    /**
     * 按照启动模式, 启动一个ViewHelper
     *
     * @param vhcls
     * @param launcheType {@link IVHManager#LAUNCH_TYPE_NULL }
     *                    {@link IVHManager#LAUNCH_TYPE_DEFAULT }
     *                    {@link IVHManager#LAUNCH_TYPE_BACK_TO_LAST}
     *                    {@link IVHManager#LAUNCH_TYPE_BACK_TO_FIRST}
     *                    {@link IVHManager#LAUNCH_TYPE_BRING_LAST_TO_TOP}
     *                    {@link IVHManager#LAUNCH_TYPE_BRING_FIRST_TO_TOP}
     * @see #startViewHelper(Class)
     * @see #startViewHelper(Class, int, Message)
     * @see #startViewHelper(Class, Message)
     */
    public void startViewHelper(Class<? extends ViewHelper> vhcls, int launcheType) {
        mVhManager.startViewHelper(vhcls, launcheType);
    }

    /**
     * 按照启动模式, 启动一个ViewHelper
     *
     * @param vhcls
     * @param launcheType {@link IVHManager#LAUNCH_TYPE_NULL }
     *                    {@link IVHManager#LAUNCH_TYPE_DEFAULT }
     *                    {@link IVHManager#LAUNCH_TYPE_BACK_TO_LAST}
     *                    {@link IVHManager#LAUNCH_TYPE_BACK_TO_FIRST}
     *                    {@link IVHManager#LAUNCH_TYPE_BRING_LAST_TO_TOP}
     *                    {@link IVHManager#LAUNCH_TYPE_BRING_FIRST_TO_TOP}
     * @param msg         <br>
     * @see #startViewHelper(Class)
     * @see #startViewHelper(Class, int)
     * @see #startViewHelper(Class, Message)
     */
    public void startViewHelper(Class<? extends ViewHelper> vhcls, int launcheType, Message msg) {
        mVhManager.startViewHelper(vhcls, launcheType, msg);
    }
}
