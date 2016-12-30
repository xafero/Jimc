package com.xafero.jimc.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadUtils {

	public static <T> T get(Future<T> future, int interval, TimeUnit unit) {
		try {
			return future.get(interval, unit);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new UnsupportedOperationException(e);
		}
	}
}