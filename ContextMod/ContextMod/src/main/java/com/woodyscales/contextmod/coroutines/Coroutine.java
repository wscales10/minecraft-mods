package com.woodyscales.contextmod.coroutines;

import com.woodyscales.contextmod.coroutines.Delegates.*;

public class Coroutine {

	private final CoroutineMethod method;

	public Coroutine(CoroutineMethod method) {
		this.method = method;
	}

	public static Object DefaultMethod(Object parameter, ProgressUpdateHandler2 progressHandler)
	{
		return null;
	}

	public CoroutineRun CreateRun() {
		return CreateRun(null);
	}

	public CoroutineRun CreateRun(Object parameter) {
		return new CoroutineRun(method, parameter);
	}
}
