package com.leon.tools.store;

import android.text.TextUtils;

import com.leon.tools.obj.ILifeObj;
import com.leon.tools.stringbean.IStringBean;


/**
 * 能根据StringBeanCls的类名来进入存储
 * 
 * @author Leon
 * 
 */
public class StringBeanClsStore implements ILifeObj {

    /**
     * 实体存储器
     */
    private IStringStore mStore;

    /**
     * 添加一个{@link IStringStore}实休存储器
     * 
     * @param store
     */
    public StringBeanClsStore(IStringStore store) {
        mStore = store;
    }

    /**
     * 存储{@link IStringBean}数据体
     * 
     * @param bean
     *            目标数据
     * @return true 存储成功,false 存储失败
     */
    public boolean storeStringBean(IStringBean bean) {
        return mStore.store(bean.getClass().getName(), bean.packString());
    }

    public boolean storeClsData(Class<? extends IStringBean> cls, String data) {
        return mStore.store(cls.getName(), data);
    }

    /**
     * 读取{@link IStringBean}数据体
     * 
     * @param cls
     *            目标类
     * @return cls实例, null 未读取到相关数据
     */
    public <T extends IStringBean> T getStringBean(Class<T> cls) {
        T bean = null;
        try {
            String beanValues = mStore.get(cls.getName());
            if (!TextUtils.isEmpty(beanValues)) {
                T b = cls.newInstance();
                if (b.parserString(beanValues))
                    bean = b;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * 移除实体数据类
     * 
     * @param cls
     *            目标Cls类
     * @return true 移除成功,移除失败
     */
    public <T extends IStringBean> boolean removeStringBean(Class<T> cls) {
        return mStore.remove(cls.getName());
    }

    /**
     * 获取真实的存储类实例
     * 
     * @return 真实的存储实例,null 没有存储实例
     */
    public IStringStore getRealStore() {
        return mStore;
    }

    /**
     * 清除所有数据
     */
    public void clearAll() {
        mStore.clearAll();
    }

    @Override
    public void init() {
        mStore.init();
    }

    @Override
    public void recycle() {
        mStore.recycle();
    }

}
