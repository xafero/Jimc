package com.xafero.jimc.util;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class ReflectUtils {

	public static Method findMethod(Class<?> type, String name) {
		for (Method meth : type.getMethods())
			if (name.equals("*") || meth.getName().equalsIgnoreCase(name))
				return meth;
		return null;
	}

	public static Method findEntryMethod(Class<?> type) {
		Method meth = findMethod(type, "call");
		if (meth == null)
			meth = findMethod(type, "main");
		if (meth == null)
			meth = findMethod(type, "*");
		return meth;
	}

	public static Object[] createArgs(Method meth) {
		Class<?>[] types = meth.getParameterTypes();
		Object[] args = new Object[types.length];
		for (int i = 0; i < types.length; i++) {
			Class<?> param = types[i];
			if (param.isArray()) {
				Class<?> cmp = param.getComponentType();
				long dim = param.getName().chars().filter(c -> c == '[').count();
				args[i] = Array.newInstance(cmp, (int) dim);
			} else
				throw new UnsupportedOperationException(param.getClass().getName() + " ?!");
		}
		return args;
	}
}