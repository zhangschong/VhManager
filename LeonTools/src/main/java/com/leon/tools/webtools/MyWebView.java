package com.leon.tools.webtools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.Field;

/**
 * WebView
 */
public class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("deprecation")
    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr,
                     boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void init() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
    }


    @Override
    public void destroy() {
        ViewParent viewParent = getParent();
        if (null != viewParent && viewParent instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) viewParent;
            vg.removeView(this);
        }
        super.destroy();
        setConfigCallback((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE));//清除数据
    }


    public void setConfigCallback(WindowManager windowManager) {
        if (android.os.Build.VERSION.SDK_INT < 16) {
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
            }
        } else {
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
            } catch (IllegalAccessException e) {
//                e.printStackTrace();
            }
        }
    }


}
