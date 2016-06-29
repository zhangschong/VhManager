package com.leon.tools.stringbean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Json标签,用于标注Json内部成员,用于{@link JSONBean}内部成员
 * 
 * @author zhanghong
 * @time 2015-6-8
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONBeanField {
    /**
     * 成员key值
     * 
     * @return key值
     */
    String name();
}
