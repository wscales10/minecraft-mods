package com.woodyscales.contextmod.coroutines;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.woodyscales.contextmod.coroutines.Delegates.*;

public class MyLock<TException extends Exception> {
	private final Lock lock = new ReentrantLock(); 
	
	public void lock(Action<TException> runnable) throws TException {
		lock.lock();
		runnable.run();
		lock.unlock();
	}
	
	public Object lock(Func<TException> runnable) throws TException {
		lock.lock();
		var output = runnable.run();
		lock.unlock();
		return output;
	}
}
