package com.leon.tools.systemutils;

import com.leon.tools.obj.ILifeObj;

/**
 * 短信读取观察者管理者, 支持多个观察者, 用于支持短信读取
 *
 * @author zhanghong
 * @time 2015-6-8
 */
public interface ISmsObserverManager extends ILifeObj {
    /**
     * 注册观察者
     *
     * @param observer 观察者
     */
    void registSmsObserver(SmsObserver observer);

    /**
     * 注销观察者
     *
     * @param observer 观察者
     */
    void unregistSmsObserver(SmsObserver observer);

    /**
     * 短信读取观察者
     *
     * @author zhanghong
     * @time 2015-6-8
     */
    public static interface SmsObserver {
        boolean onReceivedSms(String address, String content);
    }
}
