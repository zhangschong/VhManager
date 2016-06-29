package com.leon.tools.filemanager;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;

public class DeleteFileOperator implements IFileManager.IFileOperator {

    private String path = "";

    private long nowTime, timestamp;

    public DeleteFileOperator(String path, long timestamp) {
        this(path, System.currentTimeMillis(), timestamp);
    }


    public DeleteFileOperator(String path, long nowTime, long timestamp) {
        this.path = Environment.getExternalStorageDirectory() + path;
        this.nowTime = nowTime;
        this.timestamp = timestamp;
    }

    private class TimeFilefilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (nowTime - file.lastModified() > timestamp) {
                return true;
            } else {
                return false;
            }
        }

    }

    // 删除文件
    public void removeDir(File dir) {
        for (File file : dir.listFiles(new TimeFilefilter())) {
            // 不是隐藏 并且还是目录
            if (!file.isHidden() && file.isDirectory())
                removeDir(file);
            else
                file.delete();
        }
        if (dir.isDirectory() && dir.list().length > 0) {

        } else {
            dir.delete();
        }
    }

    @Override
    public boolean operator() {
        if (TextUtils.isEmpty(path))
            return false;
        File file = new File(path);
        if (!file.exists())
            return false;
        removeDir(file);
        return true;
    }

    @Override
    public String getUniqueKey() {
        return getClass().getName() + path + nowTime + timestamp;
    }

}
