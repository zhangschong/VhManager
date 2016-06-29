package com.leon.tools.systemutils;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SmsObserverManager extends ContentObserver implements ISmsObserverManager {
    public static final String SMS_URI_INBOX = "content://sms/inbox";

    private List<SmsObserver> mSmsObservers = new Vector<SmsObserver>();
    private Context mContext;
    private boolean isInit;

    public SmsObserverManager(Context context, Handler handler) {
        super(handler);
        mContext = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        if (mSmsObservers.size() > 0) {
            Cursor cursor = mContext.getContentResolver().query(Uri.parse(SMS_URI_INBOX),
                    new String[]{"address", "body"}, "read=?", new String[]{"0"}, "date desc limit 1");
            if (null != cursor) {
                final List<SmsObserver> observers = new ArrayList<SmsObserver>(mSmsObservers);// 需要回调的数量
                while (cursor.moveToNext()) {
                    List<SmsObserver> cbObservers = new ArrayList<SmsObserver>(observers);// 需要回调的个数
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    String content = cursor.getString(cursor.getColumnIndex("body"));
                    for (SmsObserver ob : cbObservers) {
                        if (ob.onReceivedSms(address, content)) {
                            observers.remove(ob);
                        }
                    }
                    if (observers.size() == 0) {// 如果已经无回调,则返回空
                        break;
                    }
                }
                cursor.close();
                cursor = null;
            }
        }
    }

    @Override
    public void init() {
        if (!isInit) {
            isInit = true;
            mSmsObservers.clear();
            mContext.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, this);
            onInit();
        }
    }

    @Override
    public void registSmsObserver(SmsObserver observer) {
        if (!mSmsObservers.contains(observer)) {
            mSmsObservers.add(observer);
        }
    }

    @Override
    public void unregistSmsObserver(SmsObserver observer) {
        mSmsObservers.remove(observer);
    }

    protected void onInit() {
        // Do nothing. Subclass should override.
    }

    protected void onRecycle() {
        // Do nothing. Subclass should override.
    }

    @Override
    public void recycle() {
        if (isInit) {
            onRecycle();
            mContext.getContentResolver().unregisterContentObserver(this);
            mSmsObservers.clear();
            isInit = false;
        }
    }

}
