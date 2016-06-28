package com.leon.tool.demo;

import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.leon.tools.fgmanager.IVHManager;
import com.leon.tools.fgmanager.ViewHelper;
import com.leon.tools.fgmanager.ViewHelperTag;

/**
 * Created by leon.zhang on 2016/6/28.
 */
public class BaseViewHelper extends ViewHelper {
    private String mTitle;

    private BaseViewHelper(String title) {
        mTitle = title;
    }


    @Override
    protected void onNewMessageCome(Message msg) {
    }

    @Override
    protected void onSaveMessage(Message msg, Bundle outState) {
        outState.putString("msg_obj", msg.obj.toString());
    }

    @Override
    protected void onRestoreMessage(Message msg, Bundle savedInstanceState) {
        msg.obj = savedInstanceState.getString("msg_obj", "");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        TextView tv = findViewById(R.id.tv_title);
        outState.putString("tv_title", tv.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        String txt = savedInstanceState.getString("tv_title", "");
        TextView tv = findViewById(R.id.tv_title);
        tv.setText(txt);
    }

    @Override
    protected View onCreateView(Context context) {
        return View.inflate(context, R.layout.test_layout, null);
    }

    @Override
    protected void onInit() {
        findViewById(R.id.tv_default).setOnClickListener(mClickListener);
        findViewById(R.id.tv_back_to_last).setOnClickListener(mClickListener);
        findViewById(R.id.tv_back_to_first).setOnClickListener(mClickListener);
        findViewById(R.id.tv_bring_last_to_top).setOnClickListener(mClickListener);
        findViewById(R.id.tv_bring_first_to_top).setOnClickListener(mClickListener);
        findViewById(R.id.tv_message).setOnClickListener(mClickListener);
        findViewById(R.id.tv_change_screen_orientation).setOnClickListener(mClickListener);
        TextView tv = findViewById(R.id.tv_title);
        String title = mTitle;
        if (null != mMessage && null != mMessage.obj)
            title = mMessage.obj.toString();
        tv.setText(title);
    }

    @Override
    protected void onResume() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = mActivity.getFragmentManager();
                IVHManager vhm = getVhManager();
                mUIController.setTextToTextView(R.id.tv_stack_size, "fg size: " + fm.getBackStackEntryCount() + "----- vh size: " + vhm.viewHelperSize());
            }
        });
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        boolean isPortrait = true;

        @Override
        public void onClick(View v) {
            Class<? extends ViewHelper> vhCls = null;
            Message msg = null;
            switch (v.getId()) {
                case R.id.tv_change_screen_orientation:
                    isPortrait = !isPortrait;
                    mActivity.setRequestedOrientation(isPortrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case R.id.tv_back_to_last:
                    vhCls = BackToLastVh.class;
                    break;
                case R.id.tv_back_to_first:
                    vhCls = BackToFristVh.class;
                    break;
                case R.id.tv_bring_last_to_top:
                    vhCls = BringLastToTopVh.class;
                    break;
                case R.id.tv_bring_first_to_top:
                    vhCls = BringFirstToTopVh.class;
                    break;
                case R.id.tv_message:
                    msg = Message.obtain();
                    msg.obj = "hahahah test one shot";
                default:
                    vhCls = DefaultVh.class;
                    break;
            }
            if (null != vhCls) {
                startViewHelper(vhCls, msg);
            }

        }
    };

    /**
     * launch model 为 {@link IVHManager#LAUNCH_TYPE_DEFAULT}
     */
    public static class DefaultVh extends BaseViewHelper {
        final static String TAG = "default";
        static int index = 0;

        public DefaultVh() {
            super(TAG + index++);
        }
    }

    /**
     * launch model 为 {@link IVHManager#LAUNCH_TYPE_BACK_TO_LAST}
     */
    @ViewHelperTag(launchModel = IVHManager.LAUNCH_TYPE_BACK_TO_LAST)
    public static class BackToLastVh extends BaseViewHelper {
        final static String TAG = "BackToLastVh";
        static int index = 0;

        public BackToLastVh() {
            super(TAG + index);
        }
    }

    /**
     * launch model 为 {@link com.leon.tools.fgmanager.IVHManager#LAUNCH_TYPE_BACK_TO_FIRST}
     */
    @ViewHelperTag(launchModel = IVHManager.LAUNCH_TYPE_BACK_TO_FIRST)
    public static class BackToFristVh extends BaseViewHelper {
        final static String TAG = "BackToFristVh";
        static int index = 0;

        public BackToFristVh() {
            super(TAG + index);
        }
    }


    /**
     * launch model 为 {@link IVHManager#LAUNCH_TYPE_BRING_LAST_TO_TOP}
     */
    @ViewHelperTag(launchModel = IVHManager.LAUNCH_TYPE_BRING_LAST_TO_TOP)
    public static class BringLastToTopVh extends BaseViewHelper {
        final static String TAG = "BringLastToTopVh";
        static int index = 0;

        public BringLastToTopVh() {
            super(TAG + index);
        }
    }

    /**
     * launch model 为 {@link IVHManager#LAUNCH_TYPE_BRING_FIRST_TO_TOP}
     */
    @ViewHelperTag(launchModel = IVHManager.LAUNCH_TYPE_BRING_FIRST_TO_TOP)
    public static class BringFirstToTopVh extends BaseViewHelper {
        final static String TAG = "BringFirstToTopVh";
        static int index = 0;

        public BringFirstToTopVh() {
            super(TAG + index);
        }
    }
}
