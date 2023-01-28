package com.woodyscales.contextmod.tasks;

import java.util.concurrent.Callable;

import com.woodyscales.contextmod.coroutines.TaskStatus;

public class GenericTask<T> {

	private final Callable<T> callable;
	private Thread thread;
	private TaskStatus status = TaskStatus.Created;
	private T result;

	private GenericTask(Callable<T> callable) {
		this.callable = callable;
	}

	public GenericTask<T> run() {
		if (status != TaskStatus.Created) {
			throw new UnsupportedOperationException();
		}

		status = TaskStatus.WaitingToRun;
		thread = new Thread(() -> {
			try {
				result = callable.call();
			} catch (Exception e) {
				e.printStackTrace();

				if (e instanceof InterruptedException) {
					status = TaskStatus.Canceled;
				} else {
					status = TaskStatus.Faulted;
				}

				return;
			}

			status = TaskStatus.RanToCompletion;
		});
		status = TaskStatus.Running;
		thread.start();
		return this;
	}

	public T await() throws InterruptedException {
		thread.join();
		return result;
	}

	public static <T> GenericTask<T> run(Callable<T> callable) {
		return new GenericTask<T>(callable).run();
	}

	public TaskStatus getStatus() {
		return status;
	}
}