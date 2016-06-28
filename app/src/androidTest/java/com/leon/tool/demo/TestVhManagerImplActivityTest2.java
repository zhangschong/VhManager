package com.leon.tool.demo;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leon.tools.fgmanager.IVHManager;
import com.leon.tools.fgmanager.ViewHelper;
import com.leon.tools.fgmanager.ViewHelperTag;

/**
 * Created by leon.zhang on 2016/6/27.
 * 测试启动中的五种模式
 */
public class TestVhManagerImplActivityTest2 extends VhManagerImplActivityBaseTestCase {

    private static class BaseViewHelper extends ViewHelper {
        String mLaunchType;
        int mInstanceIndex = 0;
        private int resumeCounts, iniCounts, recycleCounts, pauseCounts;


        BaseViewHelper(String launchType, int instanceIndex) {
            mLaunchType = launchType;
            mInstanceIndex = instanceIndex;
        }

        @Override
        protected View onCreateView(Context context) {
            TextView tv = new TextView(context);
            tv.setText(mLaunchType + " " + mInstanceIndex);
            return tv;
        }

        @Override
        protected void onInit() {
            iniCounts++;
        }

        @Override
        protected void onPause() {
            pauseCounts++;
            assertEquals(pauseCounts, resumeCounts);
        }

        @Override
        protected void onResume() {
            resumeCounts++;
        }

        @Override
        protected void onRecycle() {
            recycleCounts++;
            assertEquals(iniCounts, recycleCounts);
        }
    }

    /**
     * launch model 为 {@link IVHManager#LAUNCH_TYPE_DEFAULT}
     */
    public static class DefaultVh extends BaseViewHelper {
        final static String TAG = "default";
        private static int i = 0;

        public DefaultVh() {
            super("default", i++);
        }
    }

    /**
     * launch model 为 {@link IVHManager#LAUNCH_TYPE_BACK_TO_LAST}
     */
    @ViewHelperTag(launchModel = IVHManager.LAUNCH_TYPE_BACK_TO_LAST)
    public static class BackToLastVh extends BaseViewHelper {
        private static int i = 0;

        public BackToLastVh() {
            super("BackToLast", i++);
        }
    }

    /**
     * launch model 为 {@link com.leon.tools.fgmanager.IVHManager#LAUNCH_TYPE_BACK_TO_FIRST}
     */
    @ViewHelperTag(launchModel = IVHManager.LAUNCH_TYPE_BACK_TO_FIRST)
    public static class BackToFristVh extends BaseViewHelper {
        private static int i = 0;


        public BackToFristVh() {
            super("BackToFrist", i++);
        }
    }


    /**
     * launch model 为 {@link IVHManager#LAUNCH_TYPE_BRING_LAST_TO_TOP}
     */
    @ViewHelperTag(launchModel = IVHManager.LAUNCH_TYPE_BRING_LAST_TO_TOP)
    public static class BringLastToTopVh extends BaseViewHelper {
        private static int i = 0;

        public BringLastToTopVh() {
            super("BringLastToTop", i++);
        }
    }

    /**
     * launch model 为 {@link IVHManager#LAUNCH_TYPE_BRING_FIRST_TO_TOP}
     */
    @ViewHelperTag(launchModel = IVHManager.LAUNCH_TYPE_BRING_FIRST_TO_TOP)
    public static class BringFirstToTopVh extends BaseViewHelper {
        private static int i = 0;


        public BringFirstToTopVh() {
            super("BackFristToTop", i++);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        DefaultVh.i = 0;
        BackToFristVh.i = 0;
        BackToLastVh.i = 0;
        BringLastToTopVh.i = 0;
        BringFirstToTopVh.i = 0;
        super.tearDown();
    }

    public void testDefaultViewHelper() {

        assertEquals("vh manager viewHelper counts is " + 1, 1, mVhManager.viewHelperSize());

        int vhSize = 0;
        startViewHelper(DefaultVh.class);
        vhSize++;
        assertEquals("vh manager viewHelper counts is " + 2, 2, mVhManager.viewHelperSize());
        BaseViewHelper vh = mVhManager.findTopViewHelper();
        assertNotNull(vh);
        assertEquals("vh index is " + (vhSize - 1), (vhSize - 1), vh.mInstanceIndex);

        startViewHelper(DefaultVh.class);
        vhSize++;
        assertEquals("vh manager viewHelper counts is " + 3, 3, mVhManager.viewHelperSize());
        vh = mVhManager.findTopViewHelper();
        assertNotNull(vh);
        assertTrue("vh index is " + (vhSize - 1), vh.mInstanceIndex == (vhSize - 1));
        finishViewHelper(vh);

        startViewHelper(DefaultVh.class);
        vhSize++;
        assertEquals("vh manager viewHelper counts is " + 3, 3, mVhManager.viewHelperSize());
        vh = mVhManager.findTopViewHelper();
        assertNotNull(vh);
        assertTrue("vh index is " + (vhSize - 1), vh.mInstanceIndex == (vhSize - 1));

    }


    public void testBackToLastViewHelper() {
        int size = 1;
        startViewHelper(BackToLastVh.class);
        size++;
        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
        }

        startViewHelper(BackToLastVh.class);
        size = 2;
        assertEquals(" vh counts is " + size, size, mVhManager.viewHelperSize());

        BaseViewHelper bvh = mVhManager.findTopViewHelper();
        assertNotNull(bvh);
        assertEquals(" bvh index is " + 0, 0, bvh.mInstanceIndex);
        assertEquals(" bvh cls is ", bvh.getClass(), BackToLastVh.class);


        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
        }
        startViewHelper(BackToLastVh.class, IVHManager.LAUNCH_TYPE_DEFAULT);
        size++;
        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
        }
        startViewHelper(BackToLastVh.class);
        bvh = mVhManager.findTopViewHelper();
        assertEquals("vh counts is " + size, size, mVhManager.viewHelperSize());
        assertEquals(" bvh index is " + 1, 1, bvh.mInstanceIndex);
        assertEquals(" bvh cls is ", bvh.getClass(), BackToLastVh.class);
    }


    public void testBackToFirstViewHelper() {
        int size = 1;
        startViewHelper(BackToFristVh.class);
        size++;
        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
        }

        startViewHelper(BackToFristVh.class);
        size = 2;
        assertEquals(" vh counts is " + size, size, mVhManager.viewHelperSize());

        BaseViewHelper bvh = mVhManager.findTopViewHelper();
        assertNotNull(bvh);
        assertEquals(" bvh index is " + 0, 0, bvh.mInstanceIndex);
        assertEquals(" bvh cls is ", bvh.getClass(), BackToFristVh.class);


        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
        }
        startViewHelper(BackToFristVh.class, IVHManager.LAUNCH_TYPE_DEFAULT);
        size++;
        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
        }
        startViewHelper(BackToFristVh.class);
        size = 2;
        bvh = mVhManager.findTopViewHelper();
        assertEquals("vh counts is " + size, size, mVhManager.viewHelperSize());
        assertEquals(" bvh index is " + 0, 0, bvh.mInstanceIndex);
        assertEquals(" bvh cls is ", bvh.getClass(), BackToFristVh.class);
    }


    public void testBringLastToViewHelper() {
        int size = 1;
        startViewHelper(BringLastToTopVh.class);
        size++;
        assertEquals(" vh counts is " + size, size, mVhManager.viewHelperSize());
        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
            assertEquals(" vh counts is " + size, size, mVhManager.viewHelperSize());
        }

        startViewHelper(BringLastToTopVh.class);
        assertEquals(" vh counts is " + size, size, mVhManager.viewHelperSize());

        BaseViewHelper bvh = mVhManager.findTopViewHelper();
        assertNotNull(bvh);
        assertEquals(" bvh index is " + 0, 0, bvh.mInstanceIndex);
        assertEquals(" bvh cls is ", bvh.getClass(), BringLastToTopVh.class);


        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
        }
        startViewHelper(BringLastToTopVh.class, IVHManager.LAUNCH_TYPE_DEFAULT);
        size++;
        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
        }
        startViewHelper(BringLastToTopVh.class);
        bvh = mVhManager.findTopViewHelper();
        assertEquals("vh counts is " + size, size, mVhManager.viewHelperSize());
        assertEquals(" bvh index is " + 1, 1, bvh.mInstanceIndex);
        assertEquals(" bvh cls is ", bvh.getClass(), BringLastToTopVh.class);

        for (int i = 0; i < size; i++) {
            mVhManager.back();
        }
    }


    public void testBringFirstToViewHelper() {
        int size = 1;
        startViewHelper(BringFirstToTopVh.class);
        size++;
        assertEquals(" vh counts is " + size, size, mVhManager.viewHelperSize());
        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
            assertEquals(" vh counts is " + size, size, mVhManager.viewHelperSize());
        }

        startViewHelper(BringFirstToTopVh.class);
        assertEquals(" vh counts is " + size, size, mVhManager.viewHelperSize());

        BaseViewHelper bvh = mVhManager.findTopViewHelper();
        assertNotNull(bvh);
        assertEquals(" bvh index is " + 0, 0, bvh.mInstanceIndex);
        assertEquals(" bvh cls is ", bvh.getClass(), BringFirstToTopVh.class);


        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
        }
        startViewHelper(BringFirstToTopVh.class, IVHManager.LAUNCH_TYPE_DEFAULT);
        size++;
        for (int i = 0; i < 4; i++) {
            startViewHelper(DefaultVh.class);
            size++;
        }
        startViewHelper(BringFirstToTopVh.class);
        bvh = mVhManager.findTopViewHelper();
        assertEquals("vh counts is " + size, size, mVhManager.viewHelperSize());
        assertEquals(" bvh index is " + 0, 0, bvh.mInstanceIndex);
        assertEquals(" bvh cls is ", bvh.getClass(), BringFirstToTopVh.class);

        for (int i = 0; i < size; i++) {
            mVhManager.back();
        }
    }


    public void testMixedWidthFragment() {
        int size = 1;
        startViewHelper(DefaultVh.class);
        size++;
        FragmentManager fm = getActivity().getFragmentManager();
        final int fgSize = 4;
        for (int i = 0; i < fgSize; i++) {
            TestFragment tfg = new TestFragment();
            fm.beginTransaction().replace(R.id.main_layout_id, tfg).addToBackStack("").commit();
            try {
                Thread.sleep(DELAY_TIME_1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals("vh counts is " + size, size, mVhManager.viewHelperSize());
        }
        startViewHelper(DefaultVh.class);
        size++;
        assertNotNull("top vh is not Null", mVhManager.findTopViewHelper());
        assertEquals("vh counts is " + size, size, mVhManager.viewHelperSize());
        back();
        size--;
        assertEquals("vh counts is " + size, size, mVhManager.viewHelperSize());
        assertNull("top vh is  Null", mVhManager.findTopViewHelper());
        for (int i = 0; i < fgSize; i++) {
            back();
            assertEquals("vh counts is " + size, size, mVhManager.viewHelperSize());
        }
        assertNotNull("top vh is not Null", mVhManager.findTopViewHelper());

    }


    private class TestFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            TextView tv = new TextView(getActivity());
            tv.setText("i'm just a fg");
            return tv;
        }
    }

}