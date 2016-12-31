package com.xafero.jimc.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import com.xafero.jimc.javac.MemJavaFileManager;
import com.xafero.jimc.javac.MemJavaFileObject;
import com.xafero.jimc.jvm.MemClassLoader;
import com.xafero.jimc.util.RunUtils;
import com.xafero.jimc.util.ThreadUtils;
import com.xafero.natra.api.INativeParams;
import com.xafero.natra.common.AbstractNativeTranslator;
import com.xafero.natra.util.ErrorMessage;

public class MemCompiler extends AbstractNativeTranslator<Class<?>, byte[]> implements Closeable {

	private final JavaCompiler compiler;
	private final Locale loc;
	private final Charset enc;
	private final MemClassLoader mem;
	private final ExecutorService pool;

	public MemCompiler(MemCompilerFactory factory) {
		super(factory);
		compiler = new EclipseCompiler();
		loc = Locale.US;
		enc = Charset.forName("UTF8");
		mem = new MemClassLoader();
		pool = Executors.newCachedThreadPool();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map<URI, Entry<Class<?>, byte[]>> translateImpl(INativeParams params) {
		Map<URI, String> sources = params.getSources();
		Writer out = params.getOutput();
		@SuppressWarnings("rawtypes")
		DiagnosticListener diag = params;
		StandardJavaFileManager smgr = compiler.getStandardFileManager(diag, loc, enc);
		MemJavaFileManager fmgr = new MemJavaFileManager(smgr, mem);
		List<MemJavaFileObject> units = toFiles(sources);
		final String jdk = "8";
		Iterable<String> options = Arrays.asList("-source", jdk, "-target", jdk);
		Iterable<String> classes = null;
		CompilationTask task = compiler.getTask(out, fmgr, diag, options, classes, units);
		Future<Boolean> future = pool.submit(task);
		ThreadUtils.get(future, 10, TimeUnit.SECONDS);
		Map<URI, Entry<Class<?>, byte[]>> res = new LinkedHashMap<>();
		for (MemJavaFileObject mjfo : units) {
			String fqn = mjfo.getFullName();
			try {
				AtomicReference<byte[]> ref = new AtomicReference<>();
				Class<?> type = mem.findClass(fqn, ref);
				URI uri = URI.create("mem:" + fqn.replace('.', '/') + ".class");
				res.put(uri, new SimpleImmutableEntry<>(type, ref.get()));
			} catch (ClassNotFoundException cnf) {
				// NO-OP!
			}
		}
		return res;
	}

	private List<MemJavaFileObject> toFiles(Map<URI, String> sources) {
		List<MemJavaFileObject> files = new LinkedList<>();
		for (Entry<URI, String> e : sources.entrySet()) {
			String url = e.getKey().getRawSchemeSpecificPart();
			url = url.replace('/', '.');
			int lidx = url.lastIndexOf('.');
			String pkg = url.substring(0, lidx);
			String name = url.substring(lidx + 1);
			String code = e.getValue();
			files.add(new MemJavaFileObject(pkg, name, code));
		}
		return files;
	}

	@Override
	public Object run(Object binary, DiagnosticListener<URI> diag) {
		try {
			return RunUtils.run((Class<?>) binary);
		} catch (Exception e) {
			diag.report(new ErrorMessage(binary, e));
			return null;
		}
	}

	@Override
	public void close() throws IOException {
		mem.close();
		pool.shutdown();
	}
}