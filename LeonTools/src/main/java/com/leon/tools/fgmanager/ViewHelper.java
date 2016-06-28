package com.leon.tools.fgmanager;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.leon.tools.obj.AUtils;
import com.leon.tools.view.UiController;


/**
 * Created by leon.zhang on 2016/6/24.<br>
 * 生命周期:
 * {@link #onInit()}    <-------> {@link Fragment#onCreate(Bundle)}
 * {@link #onResume()}  <-------> {@link Fragment#onResume()}
 * {@link #onPause()}   <-------> {@link Fragment#onPause()}
 * {@link #onRecycle()} <-------> {@link Fragment#onDestroy()}
 */
public abstract class ViewHelper {

    final static String TAG = "ViewHelper";
    final static boolean IsDebug = true;

    static void debug(boolean isDebug, Object... msgs) {
        AUtils.debug(IsDebug && isDebug, TAG, msgs);
    }

    final static int VIEW_HELPER_STATE_NULL = 0;//处于无状态
    final static int VIEW_HELPER_STATE_ATTACH = 1;//处于与Fragment连接状态
    final static int VIEW_HELPER_STATE_INITED = 1 << 1;//已经初始化

    private IVHManager mVhManager;
    private IVhFragment mFragement;
    private int mCurrentState = VIEW_HELPER_STATE_NULL;//当前状态

    protected Activity mActivity;
    protected UiController mUIController;
    protected Message mMessage;//跳转消息


    /**
     * 设置跳转消息
     *
     * @param msg
     */
    final void setMessage(Message msg) {
        if (null != msg) {
            if (null != mMessage) //回收不用的message
                mMessage.recycle();
            mMessage = Message.obtain(msg);
            onNewMessageCome(mMessage);
        }
    }

    /**
     * 获取跳转时传入的消息
     *
     * @return
     */
    public final Message getMessage() {
        return mMessage;
    }

    /**
     * 设置ViewHelper
     *
     * @param vhManager
     */
    final void setVhManager(IVHManager vhManager) {
        mVhManager = vhManager;
    }

    /**
     * 结束关闭当前ViewHelper
     */
    public final void finish() {
        mVhManager.finishViewHelper(this);
    }

    final void setFragment(IVhFragment vhFragement) {
        mFragement = vhFragement;
    }

    final IVhFragment getFragment() {
        return mFragement;
    }

    /**
     * 是否为Dialog
     *
     * @return
     */
    final boolean isDialog() {
        ViewHelperTag vhTag = getClass().getAnnotation(ViewHelperTag.class);
        if (null == vhTag)
            return false;
        return vhTag.viewHelperType() == IVHManager.VIEW_HELPER_TYPE_DIALOG;
    }

    /**
     * 初始化一个页面,将在{@link #init()}之前调用,整个生命周期只会被调用一次
     *
     * @param context
     * @return
     */
    protected abstract View onCreateView(Context context);

    /**
     * 初始化,如果未被{@link #recycle()}则只被调用一次
     */
    protected void onInit() {
    }


    /**
     * 被清除时调用
     */
    protected void onRecycle() {
    }


    /**
     * 页面激活时调用
     */
    protected void onResume() {
    }


    /**
     * 页面暂停时调用
     */
    protected void onPause() {
    }

    /**
     * 当消息被重置后会调用,调用时{@link #onCreateView(Context)}已经调用过
     *
     * @param msg
     */
    protected void onNewMessageCome(Message msg) {
    }


    /**
     * 创建一个View
     */
    final void createView() {
        if (null == mUIController) {
            View view = onCreateView(mActivity);
            if (null == view) {
                throw new RuntimeException("createView is null");
            }
            mUIController = new UiController(view);
            mUIController.init();
        }
    }

    /**
     * 创建页面时调用
     *
     * @return
     */
    final void setActivity(Activity activity) {
        mActivity = activity;
        createView();
    }

    final void init() {
        if ((mCurrentState & VIEW_HELPER_STATE_INITED) == VIEW_HELPER_STATE_INITED)
            return;
        addViewHelperState(VIEW_HELPER_STATE_INITED);//如果没有初始化,则初始化
        debug(true, "init");
        onInit();
    }

    final void resume() {
        if ((mCurrentState & VIEW_HELPER_STATE_INITED) != VIEW_HELPER_STATE_INITED)//如果未初始化,则不执行
            return;
        debug(true, "resume");
        onResume();
    }

    final void pause() {
        if ((mCurrentState & VIEW_HELPER_STATE_INITED) != VIEW_HELPER_STATE_INITED)//如果未初始化,则不执行
            return;
        debug(true, "pause");
        onPause();
    }

    final void recycle() {
        if ((mCurrentState & VIEW_HELPER_STATE_INITED) != VIEW_HELPER_STATE_INITED)
            return;
        removeViewHelperState(VIEW_HELPER_STATE_INITED);//如果已经初始化,则回收
        debug(true, "recycle");
        onRecycle();
        if (null != mMessage) {
            mMessage.recycle();
            mMessage = null;
        }
        mUIController.recycle();
        mUIController = null;
    }

    /**
     * 查找View
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T findViewById(int id) {
        return null == mUIController ? null : (T) mUIController.findViewById(id);
    }

    /**
     * 获取当前的View
     *
     * @return
     */
    public final View getMainView() {
        return null == mUIController ? null : mUIController.getView();
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

    /**
     * 将数据存入到 bundle
     *
     * @param outState
     * @see Fragment#onSaveInstanceState(Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {

    }

    /**
     * 将数据从 savedInstanceState 恢复
     *
     * @param savedInstanceState
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    /**
     * 恢复 msg
     *
     * @param msg                需要恢复的message
     * @param savedInstanceState
     */
    protected void onRestoreMessage(Message msg, Bundle savedInstanceState) {

    }

    /**
     * 将Message中的信息存入 outState
     *
     * @param msg
     * @param outState
     */
    protected void onSaveMessage(Message msg, Bundle outState) {

    }


    final void saveInstanceState(Bundle outState) {
        if ((mCurrentState & VIEW_HELPER_STATE_INITED) != VIEW_HELPER_STATE_INITED)
            return;
        debug(true, "saveInstanceState");
        onSaveInstanceState(outState);
    }

    final void restoreInstanceState(Bundle savedInstanceState) {
        if ((mCurrentState & VIEW_HELPER_STATE_INITED) != VIEW_HELPER_STATE_INITED)
            return;
        debug(true, "viewStateRestored");
        onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 添加当前ViewHelper状态
     *
     * @param state
     */
    final void addViewHelperState(int state) {
        mCurrentState = (mCurrentState & (~state)) | state;
    }

    /**
     * 移除状态
     *
     * @param state
     */
    final void removeViewHelperState(int state) {
        mCurrentState = mCurrentState & (~state);
    }
}
