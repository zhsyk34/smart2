package com.dnk.smart.kit;

// TODO: 2016-11-07
public class FormatKit {

	public static <T> T getEnum(Class<T> clazz, Integer id) {
		if (id == null) {
			return null;
		}
		T[] enums = clazz.getEnumConstants();

		if (id >= 0 && id < enums.length) {
			return enums[id];
		}
		return null;
	}

	public static <T> T getEnum(Class<T> clazz, String name) {
		if (name == null) {
			return null;
		}
		T[] enums = clazz.getEnumConstants();
		for (T t : enums) {
			if (name.equalsIgnoreCase(t.toString())) {
				return t;
			}
		}
		return null;
	}
}
