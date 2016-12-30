package com.xafero.jimc.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

public class IOUtils {

	private static final String encoding = "UTF8";

	public static String getVirtualOutput(Entry<PrintStream, ByteArrayOutputStream> e, boolean error)
			throws IOException {
		PrintStream old = e.getKey();
		ByteArrayOutputStream mem = e.getValue();
		if (error)
			System.setErr(old);
		else
			System.setOut(old);
		return new String(mem.toByteArray(), encoding);
	}

	public static Entry<PrintStream, ByteArrayOutputStream> setVirtualOutput(boolean error) {
		PrintStream old = error ? System.err : System.out;
		ByteArrayOutputStream mem = new ByteArrayOutputStream();
		if (error)
			System.setErr(new PrintStream(mem));
		else
			System.setOut(new PrintStream(mem));
		return new SimpleImmutableEntry<>(old, mem);
	}
}