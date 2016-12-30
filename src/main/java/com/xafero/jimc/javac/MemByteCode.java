package com.xafero.jimc.javac;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class MemByteCode extends SimpleJavaFileObject implements Closeable {

	private ByteArrayOutputStream out;

	public MemByteCode(String name) {
		super(URI.create("file:///" + name + ".class"), Kind.CLASS);
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return (out = new ByteArrayOutputStream());
	}

	public byte[] getBytes() {
		return out.toByteArray();
	}

	@Override
	public InputStream openInputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		if (out == null)
			return;
		out.close();
	}
}