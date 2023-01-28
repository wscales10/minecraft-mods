package com.woodyscales.contextmod.coroutines;

public class FailedCoroutineRunException extends CoroutineRunException {

	public FailedCoroutineRunException() {
		super();
	}

	public FailedCoroutineRunException(String message) {
		super(message);
	}

	public FailedCoroutineRunException(String message, Exception innerException) {
		super(message, innerException);
	}

	@Override
	protected Result getResult() {
		return Result.getFaulted();
	}
}
