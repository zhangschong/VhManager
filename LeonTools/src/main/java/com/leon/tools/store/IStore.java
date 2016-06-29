package com.leon.tools.store;

import com.leon.tools.obj.ILifeObj;

/**
 * 存储器接口
 * 
 * @author zhanghong
 * @time 2015-6-9
 * @param <S>
 *            Key
 * @param <T>
 *            Value
 */
public interface IStore<S, T> extends ILifeObj {
    /**
     * 存储数据
     * 
     * @param key
     * @param values
     * @return true 存储成功,false 存储失败
     */
    boolean store(S key, T values);

    /**
     * 通过key移除数据
     * 
     * @param key
     * @return true 移除成功,false 移除失败
     */
    boolean remove(S key);

    /**
     * 通过key获取当前对象数据
     * 
     * @param key
     * @return 当前对象数据, null 未查到相关数据
     */
    T get(S key);

    /**
     * 清除所有数据
     */
    void clearAll();
}
