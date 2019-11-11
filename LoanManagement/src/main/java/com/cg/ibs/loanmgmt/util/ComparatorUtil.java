package com.cg.ibs.loanmgmt.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

public class ComparatorUtil {
	@SuppressWarnings("rawtypes")
	public static <T> Comparator<T> getComparatorOnField(Class clazz, String fieldName) {
		Comparator<T> comparator = null;

		try {
			String getter = "get" + fieldName;
			Method m = clazz.getMethod(getter);
			comparator = (b1, b2) -> {
				int result = 0;

				try {
					Comparable value1 = (Comparable) m.invoke(b1);
					Comparable value2 = (Comparable) m.invoke(b2);
					result = value1.compareTo(value2);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					System.out.println(e.getMessage());
				}

				return result;
			};

		} catch (NoSuchMethodException | SecurityException e1) {
			System.out.println(e1.getMessage());
		}

		return comparator;
	}
}
