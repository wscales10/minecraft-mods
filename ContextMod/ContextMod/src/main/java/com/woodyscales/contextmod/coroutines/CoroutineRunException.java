package com.woodyscales.contextmod.coroutines;

public abstract class CoroutineRunException extends Exception {
	protected CoroutineRunException() {
		super();
	}
	
	protected CoroutineRunException(String message) {
		super(message);
	}

	protected CoroutineRunException(String message, Exception innerException) {
		super(message, innerException);
	}
	
	protected abstract Result getResult();
}
