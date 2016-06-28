package com.leon.tool.demo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.leon.tools.fgmanager.ViewHelper;

import java.util.HashMap;

/**
 * Created by leon.zhang on 2016/6/27.
 * 用于测试ViewHelper 在临时数据存储及还原时的情况
 */
public class TestVhSaveInstance extends VhManagerImplActivityBaseTestCase {
    private static HashMap<String, Bundle> vhDatas = new HashMap<>(10);

    public static class RestoreViewHelper extends ViewHelper {

        int index;
        String keyTag;


        @Override
        protected void onNewMessageCome(Message msg) {
            index = msg.what;
            keyTag = "RestoreViewHelper" + index;
        }

        @Override
        protected void onSaveMessage(Message msg, Bundle outState) {
        }

        @Override
        protected void onRestoreMessage(Message msg, Bundle savedInstanceState) {
        }

        @Override
        protected View onCreateView(Context context) {
            TextView tv = new TextView(context);
            tv.setText("RestoreViewHelper--");
            return tv;
        }

        String getValueTag() {
            return keyTag;
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            outState.putInt("index", index);
            vhDatas.put(keyTag, outState);
        }

        @Override
        protected void onRestoreInstanceState(Bundle savedInstanceState) {
            index = savedInstanceState.getInt("index");
        }
    }

    public void testVhSaveAndRestoreVhInstance() throws Throwable {
        Message msg = Message.obtain();
        for (int i = 0; i < 4; i++) {
            msg.what = i + 1;
            startViewHelper(RestoreViewHelper.class, msg);
        }

        final int size = getActivity().getFragmentManager().getBackStackEntryCount();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });

        Object lock = new Object();
        synchronized (lock) {
            lock.wait(3000);
        }

        setActivity(mSolo.getCurrentActivity());
        mVhManager = getActivity().getVhManager();
        int newSize;
        do {
            newSize = getActivity().getFragmentManager().getBackStackEntryCount();
            synchronized (lock) {
                lock.wait(1000);
            }
        } while (newSize != size);

        for (int i = mVhManager.viewHelperSize() - 1; i > 0; i--) {//因为第一个不是RestoreViewHelper
            RestoreViewHelper rvh = mVhManager.findViewHelperByIndex(i);
            assertNotNull("vh index of " + i, rvh);
            Bundle bundle = vhDatas.get(rvh.keyTag);
            assertEquals("store data index is ", bundle.getInt("index"), rvh.index);
        }
    }


}
