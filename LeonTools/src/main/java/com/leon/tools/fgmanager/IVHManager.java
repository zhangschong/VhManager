package com.leon.tools.fgmanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leon.tools.obj.AUtils;
import com.leon.tools.obj.ILifeObj;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by leon.zhang on 2016/6/24.
 * ViewHelper管理中心
 */
public interface IVHManager extends ILifeObj {

    /**
     * 没有启动方式,按照ViewHelper配置来算
     */
    int LAUNCH_TYPE_NULL = 0;
    /**
     * 一般启动方式,来几个启动几个实例
     */
    int LAUNCH_TYPE_DEFAULT = 1;
    /**
     * 退至最近启动的实例,如果栈中没有实例,则启动一个实例
     * <p/>
     * vha->Vhb->vhb->vha->vhc (退到 vha) =结果=> vha->vhb->vhb->vha
     */
    int LAUNCH_TYPE_BACK_TO_LAST = 2;
    /**
     * 退至最初启动的实例,如果栈中没有实例,则启动一个实例
     * vha->Vhb->vhb->vha->vhc (退到 vha) =结果=> vha
     */
    int LAUNCH_TYPE_BACK_TO_FIRST = 3;
    /**
     * 将最近启动的实例,替换到最前面,如果没有则启动一个实例
     * vha->Vhb->vhb->vha->vhc (提前 vha) =结果=> vha->Vhb->vhb->vhc->vha
     *
     */
    int LAUNCH_TYPE_BRING_LAST_TO_TOP = 4;
    /**
     * 将最初启动的实例,替换到最前面,如果没有则启动一个实例
     * vha->Vhb->vhb->vha->vhc (提前 vha) =结果=> Vhb->vhb->vha->vhc->vha
     */
    int LAUNCH_TYPE_BRING_FIRST_TO_TOP = 5;

    /**
     * 一般页面
     */
    int VIEW_HELPER_TYPE_NORMAL = 1;
    /**
     * Dialog 页面
     */
    int VIEW_HELPER_TYPE_DIALOG = 2;

    /**
     * 启动一个ViewHelper
     *
     * @param vhcls
     * @see #startViewHelper(Class, Message)
     * @see #startViewHelper(Class, int, Message)
     * @see #startViewHelper(Class, int)
     */
    void startViewHelper(Class<? extends ViewHelper> vhcls);


    /**
     * 启动一个ViewHelper,并传入跳转参数
     *
     * @param vhcls
     * @param msg
     * @see #startViewHelper(Class)
     * @see #startViewHelper(Class, int, Message)
     * @see #startViewHelper(Class, int)
     */
    void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg);

    /**
     * 按照启动模式, 启动一个ViewHelper
     *
     * @param vhcls
     * @param launcheType {@link #LAUNCH_TYPE_NULL }
     *                    {@link #LAUNCH_TYPE_DEFAULT }
     *                    {@link #LAUNCH_TYPE_BACK_TO_LAST}
     *                    {@link #LAUNCH_TYPE_BACK_TO_FIRST}
     *                    {@link #LAUNCH_TYPE_BRING_LAST_TO_TOP}
     *                    {@link #LAUNCH_TYPE_BRING_FIRST_TO_TOP}
     * @see #startViewHelper(Class)
     * @see #startViewHelper(Class, int, Message)
     * @see #startViewHelper(Class, Message)
     */
    void startViewHelper(Class<? extends ViewHelper> vhcls, int launcheType);

    /**
     * 按照启动模式, 启动一个ViewHelper
     *
     * @param vhcls
     * @param launcheType {@link #LAUNCH_TYPE_NULL }
     *                    {@link #LAUNCH_TYPE_DEFAULT }
     *                    {@link #LAUNCH_TYPE_BACK_TO_LAST}
     *                    {@link #LAUNCH_TYPE_BACK_TO_FIRST}
     *                    {@link #LAUNCH_TYPE_BRING_LAST_TO_TOP}
     *                    {@link #LAUNCH_TYPE_BRING_FIRST_TO_TOP}<br>
     * @param msg         <br>
     * @see #startViewHelper(Class)
     * @see #startViewHelper(Class, int)
     * @see #startViewHelper(Class, Message)
     */
    void startViewHelper(Class<? extends ViewHelper> vhcls, int launcheType, Message msg);

    /**
     * 关闭一个ViewHelper
     *
     * @param vh
     */
    void finishViewHelper(ViewHelper vh);

    /**
     * 回退
     *
     * @return
     */
    boolean back();

    /**
     * 当前栈中ViewHelper的数量
     *
     * @return
     */
    int viewHelperSize();

    /**
     * 通过位置获取 ViewHelper
     *
     * @param index 在 fragmentManager中的位置
     * @param <T>
     * @return 返回可能为空
     */
    @Nullable
    <T extends ViewHelper> T findViewHelperByIndex(int index);

    /**
     * 获取FragmentManager中最上面的ViewHelper,如果最上面的是ViewHelper则返回,如果不是ViewHelper则返回空
     *
     * @param <T>
     * @return
     */
    @Nullable
    <T extends ViewHelper> T findTopViewHelper();
}

class VhManagerImpl implements IVHManager {
    final static String TAG = "VhManagerImpl";
    final static boolean IsDebug = true;

    //////////////恢复数据时使用的key值
    private final static String STORE_VH_CLS_KEY = "vh_cls_key";
    private final static String STORE_VH_MSG_KEY = "vh_msg_key";
    private final static String STORE_FG_STATE_KEY = "fg_state_key";
    private final static String STORE_FG_INDEX_KEY = "fg_index_key";
    private final static String STORE_VH_DATA_KEY = "vh_data_key";
    //////////////////////

    static void debug(boolean isDebug, Object... msgs) {
        AUtils.debug(IsDebug && isDebug, TAG, msgs);
    }

    static HashMap<Activity, OnFragmentCallback> STATIC_FRAGMENT_CALL_BACKS = new HashMap<>(6);

    private final int mLayoutId;
    private final Activity mActivity;
    private final FragmentManager mFragmentManager;
    private final VhFragmentCtr mVhFragmentCtr = new VhFragmentCtr();
    private final Handler mMainThreadHandler;
    private final LaunchModelManager mLaunchModelManager = new LaunchModelManager();

    VhManagerImpl(Activity activity, FragmentManager manager, Handler mainThreadHandler, int layoutId) {
        if (null == manager || null == activity)
            throw new RuntimeException("manager and activity can't be null");
        mFragmentManager = manager;
        mActivity = activity;
        mMainThreadHandler = mainThreadHandler;
        mLayoutId = layoutId;
    }

    @Override
    public void init() {
        STATIC_FRAGMENT_CALL_BACKS.put(mActivity, mFragmentLifeCtr);
        mVhFragmentCtr.init();
    }

    @Override
    public void recycle() {
        mVhFragmentCtr.recycle();
        STATIC_FRAGMENT_CALL_BACKS.remove(mActivity);
    }

    @Override
    public void startViewHelper(final Class<? extends ViewHelper> vhcls) {
        mLaunchModelManager.startViewHelper(vhcls, LAUNCH_TYPE_NULL, null);
    }

    @Override
    public void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg) {
        mLaunchModelManager.startViewHelper(vhcls, LAUNCH_TYPE_NULL, msg);
    }

    @Override
    public void startViewHelper(Class<? extends ViewHelper> vhcls, int launcheType) {
        mLaunchModelManager.startViewHelper(vhcls, launcheType, null);
    }

    @Override
    public void startViewHelper(Class<? extends ViewHelper> vhcls, int launcheType, Message msg) {
        mLaunchModelManager.startViewHelper(vhcls, launcheType, msg);
    }


    @Override
    public void finishViewHelper(final ViewHelper vh) {
        if (vh.getFragment() != null) {
            //考滤异步问题,用主线程处理
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (vh.getFragment() != null) {
                        mVhFragmentCtr.finishViewHelper(vh);
                        popBackStatckFrom(mFragmentManager.getBackStackEntryCount() - 1);
                    }
                }
            });
        }
    }

    @Override
    public boolean back() {
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            mFragmentManager.popBackStack();
        } else {
            mActivity.finish();
        }
        return true;
    }

    @Override
    public int viewHelperSize() {
        return mVhFragmentCtr.survivalFragmentSize();
    }

    @Nullable
    @Override
    public <T extends ViewHelper> T findViewHelperByIndex(int index) {
        IVhFragment vhfg = mVhFragmentCtr.getVhFragmentByKey(index);
        if (null != vhfg) {
            return (T) vhfg.getViewHelper();
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends ViewHelper> T findTopViewHelper() {
        return findViewHelperByIndex(mFragmentManager.getBackStackEntryCount() - 1);
    }

    /*检查当前存活于栈中的VhFragment*/
    private boolean popBackStatckFrom(final int startIndex) {
        if (mVhFragmentCtr.isfragmentEmpty())
            return false;
        IVhFragment fg = null;
        int i = startIndex;
        for (; i >= 0; i--) {//如果fg与Vh并无关联,则将back到相关联的Fragment
            IVhFragment cVhFg = mVhFragmentCtr.getVhFragmentByKey(i);
            if (null == cVhFg)//如果为空,则表示有其它Fragment的加入,不进行任何处理;
                break;
            if (cVhFg.isLived())//如果当前页面处于存活状态,则不进行任何处理
                break;
            fg = cVhFg;
        }
        if (null != fg) {
            if (fg.getIndex() == 0) {
                back();
            } else {
                mFragmentManager.popBackStack(fg.getTagName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            return true;
        }
        return false;
    }


    private OnFragmentCallback mFragmentLifeCtr = new OnFragmentCallback() {


        @Override
        public void onCreate(IVhFragment fg, Bundle savedInstanceState) {
            Bundle vhDataState = null;
            if (null != savedInstanceState) {//如果需要恢复数据
                final int fgState = savedInstanceState.getInt(STORE_FG_STATE_KEY, -1);
                final int fgIndex = savedInstanceState.getInt(STORE_FG_INDEX_KEY, -1);
                if (-1 == fgIndex || -1 == fgState)
                    throw new RuntimeException("restore view helper failed ");
                fg.setIndex(fgIndex);
                if (fgState != IVhFragment.VH_STATE_NULL) {
                    final String vhClsName = savedInstanceState.getString(STORE_VH_CLS_KEY, "");
                    Class<? extends ViewHelper> vhcls;
                    try {
                        vhcls = (Class<? extends ViewHelper>) OnFragmentCallback.class.getClassLoader().loadClass(vhClsName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("no such cls on restore. clsName: " + vhClsName);
                    }
                    vhDataState = savedInstanceState.getBundle(STORE_VH_DATA_KEY);
                    if (null == vhDataState)
                        throw new RuntimeException("restore view helper failed by vhDataState is null");
                    ViewHelper vh = mVhFragmentCtr.newInstanceViewHelper(vhcls, mActivity, VhManagerImpl.this);
                    fg.setViewHelper(vh);
                    vh.setFragment(fg);
                    Message msg = savedInstanceState.getParcelable(STORE_VH_MSG_KEY);
                    vh.setMessage(msg);
                }
                mVhFragmentCtr.addVhFragmentToLocalStack(fg);
            }
            ViewHelper vh = fg.getViewHelper();
            if (null != vh) {
                vh.init();
            }
            if (null != vhDataState) {//恢复ViewHelper数据
                if (null != vh.getMessage())
                    vh.onRestoreMessage(vh.getMessage(), vhDataState);
                vh.restoreInstanceState(vhDataState);
            }
        }

        @Override
        public View onCreateView(IVhFragment fg, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewHelper vh = fg.getViewHelper();
            if (null != vh) {
                return vh.getMainView();
            }
            return null;
        }

        @Override
        public void onAttach(IVhFragment fg, Activity activity) {

        }

        @Override
        public void onStart(IVhFragment fg) {
        }

        @Override
        public void onResume(IVhFragment fg) {
            ViewHelper vh = fg.getViewHelper();
            if (null != vh) {
                vh.resume();
            }
        }

        @Override
        public void onPause(IVhFragment fg) {
            ViewHelper vh = fg.getViewHelper();
            if (null != vh) {
                vh.pause();
            }
        }

        @Override
        public void onStop(IVhFragment fg) {

        }

        @Override
        public void onDetach(IVhFragment fg) {

        }

        @Override
        public void onDestroy(IVhFragment fg) {
            ViewHelper vh = fg.getViewHelper();
            if (null != vh) {
                vh.recycle();
                mVhFragmentCtr.finishViewHelper(vh);
            }
        }

        @Override
        public void onSaveInstanceState(IVhFragment fg, Bundle outState) {
            ViewHelper vh = fg.getViewHelper();
            outState.putInt(STORE_FG_STATE_KEY, fg.getVhState());
            outState.putInt(STORE_FG_INDEX_KEY, fg.getIndex());
            if (null != vh) {
                Bundle vhDataSate = new Bundle();
                vh.saveInstanceState(vhDataSate);
                if (null != vh.getMessage()) {
                    outState.putParcelable(STORE_VH_MSG_KEY, Message.obtain(vh.getMessage()));
                    vh.onSaveMessage(vh.getMessage(), vhDataSate);
                }
                outState.putString(STORE_VH_CLS_KEY, vh.getClass().getName());
                outState.putBundle(STORE_VH_DATA_KEY, vhDataSate);
            }
        }
    };


    /*启动方式的管理者*/
    private class LaunchModelManager {

        private LaunchModel[] iLaunchModels = new LaunchModel[]{new DefaultModel(), new BackToLastModel(), new BackToFirstModel(),//<br>
                new BringLastToTopModel(), new BringFirstToTopModel()};


        void startViewHelper(Class<? extends ViewHelper> vhcls, int launcheType, Message msg) {
            if (launcheType == LAUNCH_TYPE_NULL) {
                ViewHelperTag vht = vhcls.getAnnotation(ViewHelperTag.class);
                if (null != vht) {
                    launcheType = vht.launchModel();
                }
                if (launcheType == LAUNCH_TYPE_NULL)
                    launcheType = LAUNCH_TYPE_DEFAULT;
            }
            int index = launcheType - 1;
            if (index >= iLaunchModels.length) {
                index = 0;
            }
            iLaunchModels[index].startViewHelper(vhcls, msg);
        }
    }

    //////////////////////////////////////////////启动方式部份
    private interface LaunchModel {
        /* 启动方式*/
        void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg);
    }

    //同LAUNCH_TYPE_DEFAULT
    private class DefaultModel implements LaunchModel {

        protected final IVhFragment findVhFragmentByReverseOrder(Class<? extends ViewHelper> vhcls) {
            // 倒序查询,因为栈的数量不会很多,所以直接遍历
            for (int i = mFragmentManager.getBackStackEntryCount() - 1; i >= 0; i--) {
                IVhFragment vhFg = mVhFragmentCtr.getVhFragmentByKey(i);
                if (null == vhFg)
                    continue;
                ViewHelper vh = vhFg.getViewHelper();
                if (null == vh)
                    continue;
                if (vh.getClass().equals(vhcls))
                    return vhFg;
            }
            return null;
        }

        protected final IVhFragment findVhFragmentByOrder(Class<? extends ViewHelper> vhcls) {
            // 正序查询,因为栈的数量不会很多,所以直接遍历
            final int size = mFragmentManager.getBackStackEntryCount();
            for (int i = 0; i < size; i++) {
                IVhFragment vhFg = mVhFragmentCtr.getVhFragmentByKey(i);
                if (null == vhFg)
                    continue;
                ViewHelper vh = vhFg.getViewHelper();
                if (null == vh)
                    continue;
                if (vh.getClass().equals(vhcls))
                    return vhFg;
            }
            return null;
        }

        protected final IVhFragment changedVeiwHelperToNewFragment(ViewHelper vh) {
            IVhFragment oVhFg = vh.getFragment();
            oVhFg.setViewHelper(null);//解除Fg与连接关系
            vh.setFragment(null);
            IVhFragment nVhFg = VhFragmentCtr.newInstanceFragment(vh, mFragmentManager.getBackStackEntryCount(), mFragmentLifeCtr);
            vh.setFragment(nVhFg);
            VhManagerImpl.debug(true, "changedVeiwHelperToNewFragment: index-", nVhFg.getIndex());
            return nVhFg;
        }

        @Override
        public void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg) {
            IVhFragment vhFg = mVhFragmentCtr.getVhFragment(vhcls, mActivity, VhManagerImpl.this, mFragmentManager.getBackStackEntryCount(), mFragmentLifeCtr);
            vhFg.getViewHelper().setMessage(msg);
            startViewHelper(vhFg);

        }

        /**
         * 启动一个装载有ViewHelper的Fragment
         *
         * @param ivf
         */
        protected final void startViewHelper(IVhFragment ivf) {
            if (null != ivf) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                if (ivf.getViewHelper().isDialog())//如果为弹框,则启动弹框模式
                    ft.add(mLayoutId, (Fragment) ivf, ivf.getTagName());
                else
                    ft.replace(mLayoutId, (Fragment) ivf, ivf.getTagName());
                ft.addToBackStack(ivf.getTagName());
                ft.commit();
            }
        }
    }

    //同LAUNCH_TYPE_BACK_TO_LAST
    private class BackToLastModel extends DefaultModel {
        @Override
        public void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg) {
            IVhFragment vhFg = findVhFragmentByReverseOrder(vhcls);
            if (null != vhFg) {
                if (vhFg.getIndex() == mFragmentManager.getBackStackEntryCount() - 1)//如果在最上面则不进行任何处理
                    return;
                vhFg.getViewHelper().setMessage(msg);
                mFragmentManager.popBackStack(vhFg.getTagName(), 0);
            } else {
                super.startViewHelper(vhcls, msg);
            }
        }
    }

    //同LAUNCH_TYPE_BACK_TO_FIRST
    private class BackToFirstModel extends DefaultModel {
        @Override
        public void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg) {
            IVhFragment vhFg = findVhFragmentByOrder(vhcls);
            if (null != vhFg) {
                if (vhFg.getIndex() == mFragmentManager.getBackStackEntryCount() - 1)//如果在最上面则不进行任何处理
                    return;
                vhFg.getViewHelper().setMessage(msg);
                mFragmentManager.popBackStack(vhFg.getTagName(), 0);
            } else {
                super.startViewHelper(vhcls, msg);
            }
        }
    }

    //LAUNCH_TYPE_BRING_LAST_TO_TOP
    private class BringLastToTopModel extends DefaultModel {
        @Override
        public void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg) {
            IVhFragment vhFg = findVhFragmentByReverseOrder(vhcls);
            if (null != vhFg) {
                if (vhFg.getIndex() == mFragmentManager.getBackStackEntryCount() - 1)//如果在最上面则不进行任何处理
                    return;
                vhFg = changedVeiwHelperToNewFragment(vhFg.getViewHelper());
                mVhFragmentCtr.addVhFragmentToLocalStack(vhFg);
                vhFg.getViewHelper().setMessage(msg);
                startViewHelper(vhFg);
            } else {
                super.startViewHelper(vhcls, msg);
            }
        }
    }

    //LAUNCH_TYPE_BRING_FIRST_TO_TOP
    private class BringFirstToTopModel extends DefaultModel {
        @Override
        public void startViewHelper(Class<? extends ViewHelper> vhcls, Message msg) {
            IVhFragment vhFg = findVhFragmentByOrder(vhcls);
            if (null != vhFg) {
                if (vhFg.getIndex() == mFragmentManager.getBackStackEntryCount() - 1)//如果在最上面则不进行任何处理
                    return;
                vhFg = changedVeiwHelperToNewFragment(vhFg.getViewHelper());
                mVhFragmentCtr.addVhFragmentToLocalStack(vhFg);
                vhFg.getViewHelper().setMessage(msg);
                startViewHelper(vhFg);
            } else {
                super.startViewHelper(vhcls, msg);
            }
        }
    }


}

/**
 * VHmanager 的 Fragment管理部份
 */
class VhFragmentCtr implements ILifeObj {
    private ReentrantReadWriteLock.ReadLock mReadLock;
    private ReentrantReadWriteLock.WriteLock mWriteLock;
    private SparseArray<IVhFragment> mFragmentStack = new SparseArray<>();

    static ViewHelper newInstanceViewHelper(Class<? extends ViewHelper> vhCls, Activity activity, IVHManager manager) {
        ViewHelper vh = null;
        if (null == vh) {
            try {
                vh = vhCls.newInstance();
                vh.setActivity(activity);
                vh.setVhManager(manager);
                vh.createView();
            } catch (Exception e) {
                vh = null;
            }
        }
        return vh;
    }

    static IVhFragment newInstanceFragment(ViewHelper vh, int index, OnFragmentCallback cb) {
        IVhFragment fg = vh.isDialog() ? new VhFragments.DialogVhFragment() : new VhFragments.VhFragment();
        fg.setIndex(index);
        fg.setViewHelper(vh);
        return fg;
    }

    private static ViewHelper newInstanceViewHelper(Class<? extends ViewHelper> vhCls, Activity activity, IVHManager manager, int index, OnFragmentCallback cb) {
        if (null == vhCls)
            throw new RuntimeException("vhCls can't be null");
        ViewHelper vh = newInstanceViewHelper(vhCls, activity, manager);
        if (null != vh) {
            vh.setFragment(newInstanceFragment(vh, index, cb));
        }
        return vh;
    }

    @Override
    public void init() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
        mReadLock = lock.readLock();
        mWriteLock = lock.writeLock();
    }

    /*当前Fragment个数*/
    final int fragmentsize() {
        return mFragmentStack.size();
    }

    /**
     * 当前还存活的Fragment
     *
     * @return
     */
    final int survivalFragmentSize() {
        int count = 0;
        for (int i = mFragmentStack.size() - 1; i >= 0; i--) {
            IVhFragment fg = getVhFragmentByKeyIndex(i);
            if (fg.isLived()) {
                count++;
            }
        }
        return count;
    }

    final boolean isfragmentEmpty() {
        return mFragmentStack.size() == 0;
    }

    /**
     * 获取一个VhFragment
     *
     * @param vhCls
     * @param activity
     * @param manager
     * @param index
     * @param cb
     * @return
     */
    IVhFragment getVhFragment(Class<? extends ViewHelper> vhCls, Activity activity, IVHManager manager, int index, OnFragmentCallback cb) {
        return addVhFragmentToLocalStack(newInstanceViewHelper(vhCls, activity, manager, index, cb).getFragment());
    }

    /**
     * 添加一个VhFragment 到 stack
     *
     * @param vhFg
     * @return
     */
    final IVhFragment addVhFragmentToLocalStack(IVhFragment vhFg) {
        if (null != vhFg) {
            VhManagerImpl.debug(true, "getVhFragment: index-", vhFg.getIndex());
            mWriteLock.lock();
            mFragmentStack.put(vhFg.getIndex(), vhFg);
            mWriteLock.unlock();
            return vhFg;
        }
        return null;
    }

    /**
     * 获取 key 的VhFragment
     *
     * @param key
     * @return
     */
    IVhFragment getVhFragmentByKey(int key) {
        IVhFragment vhFg;
        mReadLock.lock();
        vhFg = mFragmentStack.get(key);
        mReadLock.unlock();
        return vhFg;
    }

    IVhFragment getVhFragmentByKeyIndex(int index) {
        IVhFragment vhFg;
        mReadLock.lock();
        vhFg = mFragmentStack.valueAt(index);
        mReadLock.unlock();
        return vhFg;
    }

    /**
     * 关闭当前的ViewHelper
     *
     * @param vh 目标ViewHelper
     */
    void finishViewHelper(ViewHelper vh) {
        IVhFragment vhFg = vh.getFragment();
        vh.removeViewHelperState(ViewHelper.VIEW_HELPER_STATE_ATTACH);
        vhFg.setViewHelper(null);//解除Fg与连接关系
        vh.pause();
        vh.recycle();
        vh.setFragment(null);
        VhManagerImpl.debug(true, "finishViewHelper: index-", vhFg.getIndex());
    }

    @Override
    public void recycle() {
        mFragmentStack.clear();
    }
}


