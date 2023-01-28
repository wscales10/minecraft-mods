package com.woodyscales.contextmod.coroutines;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ManualResetEvent {
	private boolean state;
	private final Lock lock = new ReentrantLock();

	public ManualResetEvent(boolean initialState) {
		state = initialState;
	}

	public void waitOne() throws InterruptedException {
		lock.lock();
		if (!state) {
			lock.unlock();
			this.wait();
		} else {
			lock.unlock();
		}
	}

	public synchronized void set() {
		lock.lock();
		if (!state) {
			state = true;
			this.notifyAll();
		}
		lock.unlock();
	}

	public void reset() {
		lock.lock();
		state = false;
		lock.unlock();
	}
}
