package com.xafero.jimc.jvm;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.xafero.jimc.javac.MemByteCode;

public class MemClassLoader extends ClassLoader implements Closeable {

	private final Map<String, MemByteCode> classes;

	public MemClassLoader() {
		classes = new HashMap<>();
	}

	public void setClass(String name, MemByteCode bytes) {
		classes.put(name, bytes);
	}

	@Override
	public void close() throws IOException {
		classes.clear();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		AtomicReference<byte[]> ref = new AtomicReference<>();
		return findClass(name, ref);
	}

	public Class<?> findClass(String name, AtomicReference<byte[]> ref) throws ClassNotFoundException {
		String path = name.replace('.', '/');
		MemByteCode wrapper = classes.get(path);
		if (wrapper == null)
			return super.findClass(name);
		byte[] bytes = wrapper.getBytes();
		ref.set(bytes);
		name = name.replace('/', '.');
		return defineClass(name, bytes, 0, bytes.length);
	}
}