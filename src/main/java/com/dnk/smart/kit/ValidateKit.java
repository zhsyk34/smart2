package com.dnk.smart.kit;

import java.util.Collection;
import java.util.Map;

public class ValidateKit {

	public static boolean empty(String str) {
		return str == null || str.isEmpty();
	}

	public static boolean notEmpty(String str) {
		return !empty(str);
	}

	public static boolean empty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean notEmpty(Collection<?> collection) {
		return !empty(collection);
	}

	public static boolean empty(Map<?, ?> map) {
		return (map == null || map.isEmpty());
	}

	public static boolean notEmpty(Map<?, ?> map) {
		return !empty(map);
	}

	public static boolean empty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean notEmpty(Object[] array) {
		return !empty(array);
	}

	public static boolean invalid(Long key) {
		return key == null || key <= 0;
	}

	public static boolean valid(Long key) {
		return !invalid(key);
	}
}
