package com.xafero.jimc.javac;

import java.io.IOException;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import com.xafero.jimc.jvm.MemClassLoader;

import javax.tools.StandardJavaFileManager;

public class MemJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

	private final MemClassLoader ldr;

	public MemJavaFileManager(StandardJavaFileManager mgr, MemClassLoader ldr) {
		super(mgr);
		this.ldr = ldr;
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location loc, String name, Kind kind, FileObject obj)
			throws IOException {
		MemByteCode bits = new MemByteCode(name);
		ldr.setClass(name, bits);
		return bits;
	}

	@Override
	public ClassLoader getClassLoader(Location loc) {
		return ldr;
	}
}