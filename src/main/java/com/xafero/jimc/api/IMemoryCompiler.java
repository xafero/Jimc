package com.xafero.jimc.api;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.Diagnostic;

public interface IMemoryCompiler {

	Map<URI, Entry<Class<?>, byte[]>> compile(Map<URI, String> sources);

	List<Diagnostic<?>> getMessages();

	String getOutput();
}