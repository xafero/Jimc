package com.xafero.jimc.impl;

import com.xafero.natra.api.INativeTranslator;
import com.xafero.natra.common.AbstractNativeFactory;

public class MemCompilerFactory extends AbstractNativeFactory {

	public MemCompilerFactory() {
		extensions = new String[] { "java" };
		mimeTypes = new String[] { "text/asm" };
	}

	@Override
	public INativeTranslator<?, ?> getTranslator() {
		return new MemCompiler(this);
	}
}