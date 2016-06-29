package com.leon.tools.store;


import com.leon.tools.utils.AndroidEncryptUtil;

/**
 * 使用装饰模式,对IStringStore进行加密处理
 *
 * @time 2015-7-8
 */
public class SafeStringStore implements IStringStore {

    private IStringStore mRealStore;

    public SafeStringStore(IStringStore store) {
        mRealStore = store;
    }

    @Override
    public boolean store(String key, String values) {
        return mRealStore.store(key, encrypt(values));
    }

    /**
     * 加密
     *
     * @param values ：文本
     * @return
     */
    private String encrypt(String values) {
        values = AndroidEncryptUtil.encryptAES(values, "！@#￥EQW%sdfsd");
        return values;
    }

    /**
     * 解密
     *
     * @param values 待解密内容
     * @return
     */
    private String decrypt(String values) {
        // TODO 解密处理
        values = AndroidEncryptUtil.decryptAES(values, "！@#￥EQW%sdfsd");
        return values;
    }

    @Override
    public boolean remove(String key) {
        return mRealStore.remove(key);
    }

    @Override
    public String get(String key) {
        String values = mRealStore.get(key);
        return decrypt(values);
    }

    @Override
    public void clearAll() {
        mRealStore.clearAll();
    }

    @Override
    public void init() {
        mRealStore.init();
    }

    @Override
    public void recycle() {
        mRealStore.recycle();
    }

}
