package com.leon.tools.store;

/**
 * 数组存储器
 * 
 * @author Leon
 * 
 * @param <T>
 */
public class ArrayStore<T> implements IStore<Integer, T> {

    protected Object[] arrays;
    private int maxSize;

    public ArrayStore(int size) {
        maxSize = size;
        this.arrays = new Object[size];
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public void init() {
        clearAll();
    }

    @Override
    public void recycle() {
        clearAll();
    }

    @Override
    public boolean remove(Integer key) {
        arrays[key] = null;
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(Integer key) {
        Object obj = arrays[key];
        if (null != obj) {
            return (T) obj;
        }
        return null;
    }

    @Override
    public void clearAll() {
        for (int i = arrays.length - 1; i >= 0; i--) {
            arrays[i] = null;
        }
    }

    @Override
    public boolean store(Integer key, T values) {
        if (null != values && values != arrays[key]) {
            remove(key);
            arrays[key] = values;
            return true;
        }
        return false;
    }

}
