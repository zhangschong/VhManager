package com.leon.tools.filemanager;

import android.os.Handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 缓存文件管理默认实现
 * 定时清理
 */
public class FileManager implements IFileManager {

    private HashMap<String, IFileOperator> mFileOperators;
    private Handler mThreadHandler;
    private long mIntervalTimeMills;

    /**
     * @param threadHandler 不能为空
     */
    public FileManager(Handler threadHandler) {
        this(threadHandler, 3 * 60 * 60 * 1000);//默认每三小时启动一次
    }

    public FileManager(Handler threadHandler, long intervalTimeMills) {
        mThreadHandler = threadHandler;
        mIntervalTimeMills = intervalTimeMills;
    }

    @Override
    public void init() {
        mFileOperators = new HashMap<String, IFileOperator>();
    }

    @Override
    public void recycle() {
        mThreadHandler.removeCallbacks(mProgressRunnable);
        mFileOperators.clear();
    }

    @Override
    public void register(IFileOperator file) {
        mFileOperators.put(file.getUniqueKey(), file);
    }

    @Override
    public void unRegister(IFileOperator file) {
        mFileOperators.remove(file);
    }

    @Override
    public boolean allFileStart() {
        mThreadHandler.removeCallbacks(mProgressRunnable);
        mThreadHandler.postDelayed(mProgressRunnable, 10 * 1000);//10秒后执行
        return true;
    }

    @Override
    public boolean fileStart(String key) {
        return mFileOperators.get(key).operator();
    }

    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            Iterator<Map.Entry<String, IFileOperator>> iter = mFileOperators.entrySet().iterator();
            while (iter.hasNext()) {
                iter.next().getValue().operator();
            }
            mThreadHandler.postDelayed(this, mIntervalTimeMills);
        }
    };
}
