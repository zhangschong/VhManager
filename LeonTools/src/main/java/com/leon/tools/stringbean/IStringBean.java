package com.leon.tools.stringbean;

/**
 * 字串序列化接口 {@link #parserString(String)}反序列化, {@link #packString()} 序列化
 *
 * @author zhanghong
 * @time 2015-6-8
 */
public interface IStringBean {
    /**
     * 反序列化
     *
     * @param string 需要反序列化的字串
     * @return true 反序列化成功, false 反序列化失败
     */
    boolean parserString(String string);

    /**
     * 打包成为对像, 一般返回空
     *
     * @return
     */
    Object packToObject();

    /**
     * 序列化
     *
     * @return 序列化后的字串
     */
    String packString();
}
