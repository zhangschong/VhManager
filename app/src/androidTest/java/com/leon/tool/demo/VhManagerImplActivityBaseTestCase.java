package com.leon.tool.demo;

import android.os.Message;
import android.test.ActivityInstrumentationTestCase2;

import com.leon.tools.fgmanager.IVHManager;
import com.leon.tools.fgmanager.ViewHelper;
import com.robotium.solo.Solo;

/**
 * Created by leon.zhang on 2016/6/27.
 */
public class VhManagerImplActivityBaseTestCase extends ActivityInstrumentationTestCase2<TestVhManagerImplActivity> {
    protected final static int DELAY_TIME_1 = 300;
    protected Solo mSolo;
    IVHManager mVhManager;

    public VhManagerImplActivityBaseTestCase() {
        super(TestVhManagerImplActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mVhManager = getActivity().getVhManager();
        mSolo = new Solo(getInstrumentation(), getActivity());
        assertNotNull(mVhManager);
    }

    protected void startViewHelper(Class<? extends ViewHelper> vhCls) {
        startViewHelper(vhCls, IVHManager.LAUNCH_TYPE_NULL, null);
    }

    protected void startViewHelper(Class<? extends ViewHelper> vhCls, Message msg) {
        startViewHelper(vhCls, IVHManager.LAUNCH_TYPE_NULL, msg);
    }

    protected void startViewHelper(Class<? extends ViewHelper> vhCls, int launchModel) {
        startViewHelper(vhCls, launchModel, null);
    }

    protected void startViewHelper(Class<? extends ViewHelper> vhCls, int launchModel, Message msg) {
        mVhManager.startViewHelper(vhCls, launchModel, msg);
        try {
            Thread.sleep(DELAY_TIME_1);//因为线程不同步,所以暂停一个短时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void back() {
        mVhManager.back();
        try {
            Thread.sleep(DELAY_TIME_1);//因为线程不同步,所以暂停一个短时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void finishViewHelper(ViewHelper vh) {
        vh.finish();
        try {
            Thread.sleep(DELAY_TIME_1);//因为线程不同步,所以暂停一个短时间
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        getActivity().finish();
        super.tearDown();
    }
}
