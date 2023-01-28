package com.woodyscales.contextmod.coroutines;

public interface IRunStateHolder {

	RunState getState();

	void throwIfNotRunning(String caller);

	void throwIfOff(String caller);

	void waitUntilOn() throws InterruptedException;

	void waitUntilRunning() throws InterruptedException;

	void waitUntilOff() throws InterruptedException;

	void waitUntilNotRunning() throws InterruptedException;

	boolean isPaused();

	boolean isRunning();

	boolean isOn();

}
