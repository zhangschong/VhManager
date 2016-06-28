package com.leon.tool.demo;

import android.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.leon.tools.fgmanager.IVHManager;
import com.leon.tools.fgmanager.ViewHelper;

/**
 * Created by leon.zhang on 2016/6/25.
 * 用于 ViewHelperManager Impl 测试用
 * 主要测试,launchType 为 {@link IVHManager#LAUNCH_TYPE_DEFAULT}情况下,栈中的情况
 */
public class TestVhManagerImplActivityTest1 extends ActivityInstrumentationTestCase2<TestVhManagerImplActivity> {

    private TestVhManagerImplActivity mTestActivity;

    public TestVhManagerImplActivityTest1() {
        super(TestVhManagerImplActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTestActivity = getActivity();
        assertNotNull(mTestActivity);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private View clickView(final View view) {
        final Object lock = new Object();
        view.post(new Runnable() {
            @Override
            public void run() {
                view.performClick();
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
        try {
            synchronized (lock) {
                lock.wait(1000);
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }

    private View clickView(int id) {
        return clickView(mTestActivity.findViewById(id));
    }

    /**
     * 测试ViewHelperActivity 页面添加
     */
    public void testAddViewHelper() {
        FragmentManager fm = mTestActivity.getFragmentManager();
        IVHManager manager = mTestActivity.getVhManager();
        assertNotNull(manager);
        assertTrue("fm back stack size is 1", fm.getBackStackEntryCount() == 1);//首页添加,由1开始增加
        assertTrue("vhm back stack size is 1", manager.viewHelperSize() == 1);
        clickView(R.id.tv_add);
        assertTrue("fm back stack size is 2", fm.getBackStackEntryCount() == 2);
        assertTrue("vhm back stack size is 2", manager.viewHelperSize() == 2);
        clickView(R.id.tv_add);
        assertTrue("fm back stack size is 3", fm.getBackStackEntryCount() == 3);
        assertTrue("vhm back stack size is 3", manager.viewHelperSize() == 3);
        clickView(R.id.tv_add);
        assertTrue("fm back stack size is 1", fm.getBackStackEntryCount() == 4);
        assertTrue("vhm back stack size is 1", manager.viewHelperSize() == 4);
    }


    public void testBackViewHelper() {
        testAddViewHelper();
        FragmentManager fm = mTestActivity.getFragmentManager();
        IVHManager manager = mTestActivity.getVhManager();
        assertNotNull(manager);
        mTestActivity.onBackPressed();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("fm back stack size is 3, fm is" + fm.getBackStackEntryCount(), fm.getBackStackEntryCount() == 3);
        assertTrue("vhm back stack size is 3, vhm is " + manager.viewHelperSize(), manager.viewHelperSize() == 3);
        mTestActivity.onBackPressed();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("fm back stack size is 2, fm is" + fm.getBackStackEntryCount(), fm.getBackStackEntryCount() == 2);
        assertTrue("vhm back stack size is 2, vhm is " + manager.viewHelperSize(), manager.viewHelperSize() == 2);
        mTestActivity.onBackPressed();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("fm back stack size is 1, fm is" + fm.getBackStackEntryCount(), fm.getBackStackEntryCount() == 1);
        assertTrue("vhm back stack size is 1, vhm is " + manager.viewHelperSize(), manager.viewHelperSize() == 1);
        mTestActivity.onBackPressed();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(mTestActivity.isFinishing());
    }


    public void testFinishViewHelper() {
        testAddViewHelper();
        FragmentManager fm = mTestActivity.getFragmentManager();
        IVHManager manager = mTestActivity.getVhManager();
        ViewHelper vh = manager.findViewHelperByIndex(4);
        assertTrue(vh == null);
        vh = manager.findViewHelperByIndex(1);
        assertNotNull(vh);
        vh.finish();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("vhm back stack size is 3, vhm is " + manager.viewHelperSize(), manager.viewHelperSize() == 3);
        assertTrue("fm back stack size is 4, fm is" + fm.getBackStackEntryCount(), fm.getBackStackEntryCount() == 4);
        vh = manager.findViewHelperByIndex(1);
        assertTrue(vh == null);
        vh = manager.findViewHelperByIndex(3);
        assertNotNull(vh);
        vh.finish();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("vhm back stack size is 2, vhm is " + manager.viewHelperSize(), manager.viewHelperSize() == 2);
        assertTrue("fm back stack size is 3, fm is" + fm.getBackStackEntryCount(), fm.getBackStackEntryCount() == 3);
        vh = manager.findViewHelperByIndex(2);
        assertNotNull(vh);
        vh.finish();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue("vhm back stack size is 1, vhm is " + manager.viewHelperSize(), manager.viewHelperSize() == 1);
        assertTrue("fm back stack size is 1, fm is" + fm.getBackStackEntryCount(), fm.getBackStackEntryCount() == 1);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}