package com.woodyscales.contextmod.coroutines;

public class Result {
	private final TaskStatus status;
	private final Object value;

	private Result(TaskStatus status, Object value) {
		this.status = status;
		this.value = value;
	}

	private static final Result faulted = new Result(TaskStatus.Faulted, null);
	
	private static final Result canceled = new Result(TaskStatus.Canceled, null);

	public static Result getFaulted() {
		return faulted;
	}
	
	public static Result getCanceled() {
		return canceled;
	}

	public static Result getCompleted(Object value) {
		return new Result(TaskStatus.RanToCompletion, value);
	}

	public boolean isSuccess() {
		return getStatus() == TaskStatus.RanToCompletion;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public Object getValue() {
		return value;
	}
}