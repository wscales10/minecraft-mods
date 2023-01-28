package com.woodyscales.contextmod.coroutines;

public class Delegates {
	@FunctionalInterface
	public interface ProgressUpdateHandler {
		boolean handle(ProgressUpdate progressUpdate);
	}

	@FunctionalInterface
	public interface ProgressUpdateHandler2 {
		void handle(ProgressUpdate progressUpdate) throws CanceledCoroutineRunException;
	}

	@FunctionalInterface
	public interface CoroutineMethod {
		Object run(Object parameter, ProgressUpdateHandler2 progressHandler) throws CoroutineRunException;
	}

	@FunctionalInterface
	public interface Action<TException extends Exception> {
		void run() throws TException;
	}
	
	@FunctionalInterface
	public interface Func<TException extends Exception> {
		Object run() throws TException;
	}
}