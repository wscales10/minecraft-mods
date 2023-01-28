package com.woodyscales.contextmod.coroutines;

import com.woodyscales.contextmod.coroutines.Delegates.*;

public class CoroutineRun
{
	private final CoroutineMethod func;

	private final Object parameter;
	
	private boolean _continue = true;
	
	private Result result;

	public CoroutineRun(CoroutineMethod func) {
		if (func == null) {
			throw new IllegalArgumentException();
		}

		this.func = func;
		this.parameter = null;
	}
	
	public CoroutineRun(CoroutineMethod func, Object parameter)
	{
		if (func == null) {
			throw new IllegalArgumentException();
		}

		this.func = func;
		this.parameter = parameter;
	}
	
	public boolean getContinue() {
		return _continue;
	}

	public void setContinue(boolean _continue) {
		this._continue = _continue;
	}

	public Result getResult() {
		return result;
	}

	public CoroutineRun ThrowOnFailure(ProgressUpdateHandler handler) throws Exception
	{
		Run(handler);
		if (getResult().isSuccess())
		{
			return this;
		} else {
			throw new FailedCoroutineRunException();
		}
	}

	public CoroutineRun Run(ProgressUpdateHandler handler) {
		Object output;
		try {
			output = func.run(parameter, progressUpdate -> {
				if (!handler.handle(progressUpdate)) {
					throw new CanceledCoroutineRunException();
				}
			});
		} catch (CoroutineRunException e) {
			result = e.getResult();
			return this;
		}

		result = Result.getCompleted(output);
		return this;

	}

	public CoroutineRun RunToCompletion() {
		return Run(progressUpdate -> true);
	}
}
