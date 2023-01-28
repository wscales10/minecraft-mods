package com.woodyscales.contextmod.tasks;

import com.woodyscales.contextmod.coroutines.TaskStatus;

public class Task {

	private final Action action;
	private Thread thread;
	private TaskStatus status = TaskStatus.Created;

	private Task(Action action) {
		this.action = action;
	}

	public Task run() {
		if (status != TaskStatus.Created) {
			throw new UnsupportedOperationException();
		}

		status = TaskStatus.WaitingToRun;
		thread = new Thread(() -> {
			try {
				action.run();
			} catch (InterruptedException e) {
				status = TaskStatus.Canceled;
				return;
			} catch (Exception e) {
				e.printStackTrace();
				status = TaskStatus.Faulted;
				return;
			}
			
			status = TaskStatus.RanToCompletion;
		});
		status = TaskStatus.Running;
		thread.start();
		return this;
	}

	public void await() throws InterruptedException {
		thread.join();
	}

	public static Task run(Action action) {
		return new Task(action).run();
	}

	public TaskStatus getStatus() {
		return status;
	}
}