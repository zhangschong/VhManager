package com.leon.tools.http;

import java.io.IOException;
import java.io.OutputStream;

class EntryTextPart extends EntryPart {

    private String mText;

    void setTextValue(String name, String text) {
        startAddContentDisposition();
        addContentDisposition(Name, name);
        setContentType("text/plain");
        mText = encode(text);
    }

    @Override
    protected void onWriteData(OutputStream os) throws IOException {
        os.write(mText.getBytes());
        os.flush();
    }

    @Override
    public boolean canWrite() {
        return null != mText;
    }

}
