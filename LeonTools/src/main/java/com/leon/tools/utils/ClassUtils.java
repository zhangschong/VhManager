package com.leon.tools.utils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Class 操作相关工具
 *
 * @author zhanghong
 * @time 2015-6-8
 */
public class ClassUtils {

    /**
     * 得到class 的泛形类
     *
     * @param cls 目标Class
     * @return 第一个泛形类, null则未查找到
     */
    public static Class<?> getParameterizedClassClass(Class<?> cls) {
        Class<?> entityClass = null;
        if (null != cls) {
            Type type = cls.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                Type[] p = ((ParameterizedType) type).getActualTypeArguments();
                entityClass = (Class<?>) p[0];
            }
        }
        return entityClass;
    }

    public static <T> T newInstanceofClass(Class<T> cls) {
        if (null != cls) {
            try {
                return cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取方法
     *
     * @param cls
     * @param name
     * @param parameterTypes
     * @return
     */
    public static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
        try {
            return cls.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

}
