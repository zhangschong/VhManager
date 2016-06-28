package com.leon.tools.fgmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by leon.zhang on 2016/6/25.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewHelperTag {
    /**
     * 启动模式
     *
     * @return
     */
    int launchModel() default IVHManager.LAUNCH_TYPE_DEFAULT;

    /**
     * ViewHelper类型
     *
     * @return
     */
    int viewHelperType() default IVHManager.VIEW_HELPER_TYPE_NORMAL;
}
