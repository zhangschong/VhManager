package com.leon.tools.stringbean;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于解析和打包Json数据
 *
 * @author Leon
 */
public class JSONBean implements IStringBean {
    private final static String TAG = "JSONBean";

    /*
     * 获取当前类中所有成员变量
     */
    private List<Field> getAllFiled() {
        return getAllFiledInJsonBean(getClass());
    }

    /**
     * 获取类中的所有成员变量
     *
     * @param cls 目标类
     * @return 目标类的所有成员变量
     */
    public final static List<Field> getAllFiledInJsonBean(Class<?> cls) {
        List<Field> fields = new ArrayList<Field>();
        while (!JSONBean.class.equals(cls)) {
            Field[] flds = cls.getDeclaredFields();
            if (null != fields) {
                for (Field fld : flds) {
                    fields.add(fld);
                }
            }
            cls = cls.getSuperclass();
        }
        return fields;
    }

    /**
     * 获取当前类型的Type
     *
     * @param type 当前Type
     * @return 实际类的类型, 如果为Class则返回Class, 如果为泛型则返回第一个泛型类型
     */
    public final static Class<?> getTypeClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getTypeClass(((ParameterizedType) type).getActualTypeArguments()[0]);
        }
        return null;
    }

    /*
     * 得到当前成员变量的数据
     * 
     * @param fld 当前field
     * 
     * @return 实例对象
     */
    private Object getValue(Field fld) {
        if (null != fld) {
            try {
                return fld.get(this);
            } catch (Exception e) {
            }
        }
        return null;
    }

    private Object checkValue(Object obj, Field fld, Object defaultValue) {
        if (null == obj) {
            obj = getValue(fld);
        } else {
            return obj;
        }
        return null == obj ? defaultValue : obj;
    }

    /*
     * 将字串解析成相关实例对象
     * 
     * @param fldType
     * 
     * @param fld
     * 
     * @param value
     * 
     * @return
     */
    private Object parserObject(Type type, Field fld, String value) throws Exception {
        Object obj = null;
        if (type instanceof Class<?>) {
            Class<?> fldType = (Class<?>) type;
            if (String.class.equals(fldType)) {// String 类型
                obj = value;
                if (TextUtils.isEmpty(value)) {
                    obj = null;
                }
                obj = checkValue(obj, fld, "");
            } else if (Double.class.equals(fldType) || type == double.class) {// Double 类型
                try {
                    obj = Double.parseDouble(value);
                } catch (Exception e) {
                    obj = null;
                }
                obj = checkValue(obj, fld, 0d);
            } else if (Integer.class.equals(fldType) || type == int.class) {// 整型
                try {
                    int radix = 10;
                    if (value.startsWith("#")) {
                        value = value.replaceFirst("#", "");
                        radix = 16;
                    } else if (value.startsWith("0x")) {
                        value = value.replaceFirst("0x", "");
                        radix = 16;
                    }// 16进制解析
                    obj = Integer.parseInt(value, radix);
                } catch (Exception e) {
                    obj = null;
                }
                obj = checkValue(obj, fld, 0);
            } else if (Float.class.equals(fldType) || type == float.class) {
                try {
                    obj = Float.parseFloat(value);
                } catch (Exception e) {
                    obj = null;
                }
                obj = checkValue(obj, fld, 0f);
            } else if (Boolean.class.equals(fldType) || type == boolean.class) {// Boolean型
                try {
                    if ("0".equals(value)) {
                        value = "false";
                    } else if ("1".equals(value)) {
                        value = "true";
                    }
                    if (!TextUtils.isEmpty(value)) {
                        obj = Boolean.parseBoolean(value);
                    }
                } catch (Exception e) {
                    obj = null;
                }
                obj = checkValue(obj, fld, false);
            } else if (Long.class.equals(fldType) || type == long.class) {// 长整型
                try {
                    obj = Long.parseLong(value);
                } catch (Exception e) {
                    obj = null;
                }
                obj = checkValue(obj, fld, 0L);
            } else if (IStringBean.class.isAssignableFrom(fldType)) {
                // IStringBean 类型
                try {
                    if (null == value)
                        value = "";
                    IStringBean bean = null;
                    if (null != fld) {
                        bean = (IStringBean) fld.get(this);
                    }
                    if (null == bean) {
                        bean = (IStringBean) fldType.newInstance();
                    }
                    if (bean.parserString(value)) {
                        obj = bean;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (List.class.isAssignableFrom(fldType)) {
                if (null == value || "".equals(value))
                    value = "[]";
                if (null != fld) {
                    obj = parserList(fldType, (ParameterizedType) fld.getGenericType(), fld, value);
                }
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            obj = parserList((Class<?>) pType.getRawType(), pType, fld, value);
        }
        return obj;
    }

    /* 解析list */
    @SuppressWarnings("unchecked")
    private List<Object> parserList(Class<?> listCls, ParameterizedType listType, Field fld, String value)
            throws Exception {
        List<Object> lists = (List<Object>) getValue(fld);

        if (value.startsWith("[")) {
            JSONArray array = new JSONArray(value);
            final int size = array.length();
            if (null == lists) {
                if (listCls == ArrayList.class) {
                    lists = new ArrayList<Object>(size);
                } else {
                    lists = (List<Object>) listCls.newInstance();
                }
            } else {
                lists.clear();
            }

            Type itemCls = listType.getActualTypeArguments()[0];
            for (int i = 0; i < size; i++) {
                String v = array.getString(i);
                lists.add(parserObject(itemCls, null, v));
            }
        } else {
            if (null == lists) {
                if (listCls == ArrayList.class) {
                    lists = new ArrayList<Object>(1);
                } else {
                    lists = (List<Object>) listCls.newInstance();
                }
            } else {
                lists.clear();
            }
            Type itemCls = listType.getActualTypeArguments()[0];
            lists.add(parserObject(itemCls, null, value));
        }
        return lists;
    }

    @Override
    public boolean parserString(String string) {
        try {
            if (TextUtils.isEmpty(string)) {
                string = "{}";
            }
            // modified by zhanghong for: 用于直接解析数组数据
            JSONObject values;
            if (string.startsWith("[")) {
                values = new JSONObject();
                values.put("array", string);
            } else {
                values = new JSONObject(string);
            }
            // modified 2015-10-12 下午6:27:48

            for (Field fld : getAllFiled()) {
                JSONBeanField bfld = fld.getAnnotation(JSONBeanField.class);
                if (null != bfld) {
                    String value = values.optString(bfld.name());
                    fld.setAccessible(true);
                    Class<?> fldType = fld.getType();
                    Object vle = parserObject(fldType, fld, value);
                    if (null != vle) {
                        fld.set(this, vle);
                    }
                    fld.setAccessible(false);
                }
            }
            return true;
        } catch (Exception e) {
            // AndroidUtils.ERROR(TAG, "-class:", getClass(), " -data:", string,
            // " -msg:", e.getMessage());
        }
        return false;
    }

    @Override
    public JSONObject packToObject() {
        JSONObject obj = new JSONObject();
        packJSONObject(obj);
        return obj;
    }

    /*
     * 将Obj序列化
     * 
     * @param obj 目标对象
     * 
     * @return 序列化后的对象
     */
    private Object packObject(Object obj) {
        Object value = null;
        if (null != obj) {
            if (obj instanceof IStringBean) {
                // IStringBean类型
                IStringBean bean = (IStringBean) obj;
                value = bean.packToObject();
                if (null == value) {
                    value = bean.packString();
                }
            } else if (obj instanceof List) {
                // 数组类型
                List<?> items = (List<?>) obj;
                JSONArray array = new JSONArray();
                for (Object o : items) {
                    array.put(packObject(o));
                }
                value = array;
            } else {
                // 基它类型
                value = obj;
            }
        }
        return value;
    }

    /**
     * 序例化数据
     *
     * @param obj 目标对象
     * @return 返回JsonObject
     */
    protected JSONObject packJSONObject(JSONObject obj) {
        for (Field fld : getAllFiled()) {
            JSONBeanField bfld = fld.getAnnotation(JSONBeanField.class);
            if (null != bfld) {
                try {
                    Object value = null;
                    fld.setAccessible(true);
                    Object o = fld.get(this);
                    value = packObject(o);
                    fld.setAccessible(false);
                    if (null == value) {
                        value = "";
                    }
                    obj.put(bfld.name(), value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        return obj;
    }

    @Override
    public String packString() {
        return packToObject().toString();
    }

}
