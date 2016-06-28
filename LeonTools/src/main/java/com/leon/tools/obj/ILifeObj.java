package com.leon.tools.obj;

/**
 * Created by leon.zhang on 2016/6/24.
 * 用于需要生命周期控制的类
 */
public interface ILifeObj {

    /**
     * 初始化时调用
     */
    void init();

    /**
     * 回收时调用
     */
    void recycle();
}
