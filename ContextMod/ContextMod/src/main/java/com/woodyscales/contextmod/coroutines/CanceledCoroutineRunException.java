package com.woodyscales.contextmod.coroutines;

public class CanceledCoroutineRunException extends CoroutineRunException {

	@Override
	protected Result getResult() {
		return Result.getCanceled();
	}
}
