package com.leon.tools.http;

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * post参数基类<br>
 * 子类: {@link EntryTextPart}, {@link EntryFilePart}
 * 
 * @author zhanghong
 * @time 2015-6-9
 */
abstract class EntryPart {
    public final static String Boundary = "--------adfasjdlfj7457454456";
    public final static String Line = "--";
    public final static String End = "\r\n";
    protected final static String Content_Disposition = "Content-Disposition: ";
    protected final static String Content_Type = "Content-Type: ";
    protected final static String Name = "name";

    private String mDisposition;
    private StringBuilder mDispositions;
    private String mContentType;

    EntryPart() {
        this("form-data");
    }

    EntryPart(String contentDisposition) {
        mDisposition = contentDisposition;
    }

    protected String encode(String value) {
        // try {
        // return URLEncoder.encode(value, "UTF-8");
        // } catch (UnsupportedEncodingException e) {
        // return value;
        // }
        return value;
    }

    /**
     * 开始添加描述
     */
    protected void startAddContentDisposition() {
        mDispositions = new StringBuilder();
    }

    /**
     * 添加post描述
     * 
     * @param name
     * @param value
     */
    protected void addContentDisposition(String name, String value) {
        mDispositions.append(encode(name));
        mDispositions.append("=\"");
        mDispositions.append(encode(value));
        mDispositions.append("\";");
    }

    /**
     * 设置内容类型
     * 
     * @param contentType
     */
    protected void setContentType(String contentType) {
        this.mContentType = contentType;
    }

    /**
     * 调用时,将内部相关参数写入
     * 
     * @param os
     * @throws IOException
     */
    public void writePart(OutputStream os) throws IOException {
        if (canWrite()) {
            StringBuilder builder = new StringBuilder();
            builder.append(Line);
            builder.append(Boundary);
            builder.append(End);
            builder.append(Content_Disposition);
            builder.append(mDisposition);
            builder.append(";");
            String ds = mDispositions.toString();
            builder.append(ds.subSequence(0, ds.length() - 1));
            builder.append(End);
            if (!TextUtils.isEmpty(mContentType)) {
                builder.append(Content_Type);
                builder.append(mContentType);
                builder.append(End);
            }
            builder.append(End);
            os.write(builder.toString().getBytes());
            onWriteData(os);
            os.write(End.getBytes());
            os.flush();
        }
    }

    /**
     * 内容是否可以写入
     * 
     * @return true 则写入, false 不写入
     */
    public abstract boolean canWrite();

    /**
     * 写入实体内容部份
     * 
     * @param os
     * @throws IOException
     */
    protected abstract void onWriteData(OutputStream os) throws IOException;

}
