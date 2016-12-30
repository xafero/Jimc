package com.xafero.jimc.util;

import static com.xafero.jimc.util.ReflectUtils.createArgs;
import static com.xafero.jimc.util.ReflectUtils.findEntryMethod;

import java.lang.reflect.Method;

public class RunUtils {

	public static Object invokeEntryMethod(Class<?> type, Method meth) throws ReflectiveOperationException {
		Object obj = type.newInstance();
		Object[] args = createArgs(meth);
		Object methRes = meth.invoke(obj, args);
		return methRes;
	}

	public static Object run(Class<?> type) throws ReflectiveOperationException {
		Method meth = findEntryMethod(type);
		Object res = invokeEntryMethod(type, meth);
		return res;
	}
}