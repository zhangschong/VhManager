package com.leon.tools.store;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * android sharedPerference Store
 * 
 * @author Leon
 * 
 */
public class SharedPerferenceStringStore implements IStringStore {

	private SharedPreferences mShared;

	public SharedPerferenceStringStore(Context context, String name) {
		mShared = context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}

	@Override
	public boolean store(String key, String values) {
		return mShared.edit().putString(key, values).commit();
	}

	@Override
	public boolean remove(String key) {
		return mShared.edit().remove(key).commit();
	}

	@Override
	public String get(String key) {
		return mShared.getString(key, "");
	}

	public Map<String, ?> getAll() {
		return mShared.getAll();
	}

	// @Override
	// public List<String> getAll() {
	// Map<String, ?> maps = mShared.getAll();
	// List<String> items = new ArrayList<String>(maps.size());
	// for (Object o : maps.values()) {
	// if (o instanceof String) {
	// items.add((String) o);
	// }
	// }
	// return items;
	// }

	@Override
	public void clearAll() {
		mShared.edit().clear().commit();
	}

	@Override
	public void init() {
	}

	@Override
	public void recycle() {
	}

}
