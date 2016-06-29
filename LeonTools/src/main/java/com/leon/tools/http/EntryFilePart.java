package com.leon.tools.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * post 上传文件参数
 * 
 * @author zhanghong
 * @time 2015-6-9
 */
class EntryFilePart extends EntryPart {
    // 上传文件类型============================================================
    public final static String FILE_TYPE_IMG_BMP = "img/bmp";
    public final static String FILE_TYPE_IMG_JPG = "img/jpeg";
    public final static String FILE_TYPE_IMG_PNG = "img/png";
    public final static String FILE_TYPE_FILE = "file";
    public final static String FILE_TYPE_OBJ = "application/octet-stream";
    // 上传文件类型============================================================

    private final static int Buffer_Size = 1024;// 1k
    protected final static String FileName = "filename";

    private byte[] buffer = new byte[Buffer_Size];

    private OnFileUploadListener mListenter;
    private File mFile;

    /**
     * 设置文件上传回调接口
     * 
     * @param listener
     */
    public void setOnFileUploadListener(OnFileUploadListener listener) {
        mListenter = listener;
    }

    /**
     * 设置上传文件
     * 
     * @param name
     *            key 值
     * @param file
     *            目标文件
     */
    public void setFile(String name, File file) {
        setFile(name, file.getName(), file);
    }

    /**
     * 设置上传文件
     * 
     * @param name
     *            key 值
     * @param fileName
     *            目标文件名
     * @param file
     *            目标文件
     */
    public void setFile(String name, String fileName, File file) {
        setFile(FILE_TYPE_FILE, name, fileName, file);
    }

    public void setFile(String fileType, String name, String fileName, File file) {
        if (file.exists()) {
            startAddContentDisposition();
            addContentDisposition(Name, name);
            addContentDisposition(FileName, fileName);
            setContentType(fileType);
            mFile = file;
        } else if (null != mListenter) {
            mListenter.onUploadFailed(file, 0, "");
            mFile = null;
        }
    }

    @Override
    protected void onWriteData(OutputStream os) throws IOException {
        FileInputStream fis = new FileInputStream(mFile);
        int len;
        if (null == mListenter) {
            try {
                len = fis.read(buffer);
                while (len >= 0) {
                    os.write(buffer, 0, len);
                    os.flush();
                    len = fis.read(buffer);
                }
            } catch (IOException e) {
                throw e;
            } finally {
                fis.close();
            }
        } else {
            try {
                final long size = mFile.length();
                long index = 0;
                len = fis.read(buffer);
                while (len >= 0) {
                    os.write(buffer, 0, len);
                    os.flush();
                    index += len;
                    mListenter.onUploadding(mFile, (int) (index * 100 / size));
                    len = fis.read(buffer);
                }
                mListenter.onUploadSucceed(mFile);
            } catch (IOException e) {
                mListenter.onUploadFailed(mFile, 0, "");
                throw e;
            } finally {
                fis.close();
            }
        }

    }

    @Override
    public boolean canWrite() {
        return null != mFile;
    }

    /**
     * 上传文件监听
     * 
     * @author zhanghong
     * @time 2015-6-9
     */
    public static interface OnFileUploadListener {
        /**
         * 上传文件失败
         * 
         * @param file
         *            目标文件
         * @param errCode
         * @param errMsg
         */
        void onUploadFailed(File file, int errCode, String errMsg);

        /**
         * 上传文件中
         * 
         * @param file
         *            目标文件
         * @param progress
         *            当前进度[0,100]
         */
        void onUploadding(File file, int progress);

        /**
         * 文件上传成功时调用
         * 
         * @param file
         *            目标文件
         */
        void onUploadSucceed(File file);
    }

}
