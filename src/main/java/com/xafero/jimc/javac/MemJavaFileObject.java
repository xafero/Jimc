package com.xafero.jimc.javac;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class MemJavaFileObject extends SimpleJavaFileObject {

	private final String pkg;
	private final String name;
	private final String code;

	public MemJavaFileObject(String pkg, String name, String code) {
		super(URI.create("file:///" + name + ".java"), Kind.SOURCE);
		this.pkg = pkg;
		this.name = name;
		this.code = code;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreErrors) {
		return code;
	}

	public String getFullName() {
		return pkg + '/' + name;
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream openInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return "[pkg=" + pkg + ", name=" + name + ", fullName=" + getFullName() + ", uri=" + uri + ", kind=" + kind
				+ "]";
	}
}