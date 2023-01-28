package com.woodyscales.contextmod.coroutines;

public class ReadOnlyRunStateHolder implements IRunStateHolder {
	private final RunStateHolder mutable;

	public ReadOnlyRunStateHolder(RunStateHolder mutable) {
		this.mutable = mutable;
	}

	@Override
	public RunState getState() {
		return mutable.getState();
	}

	@Override
	public boolean isOn() {
		return mutable.isOn();
	}

	@Override
	public boolean isRunning() {
		return mutable.isRunning();
	}

	@Override
	public boolean isPaused() {
		return mutable.isPaused();
	}

	@Override
	public void throwIfNotRunning(String caller) {
		mutable.throwIfNotRunning(caller);
	}

	@Override
	public void throwIfOff(String caller) {
		mutable.throwIfOff(caller);
	}

	@Override
	public void waitUntilNotRunning() throws InterruptedException {
		mutable.waitUntilNotRunning();
	}

	@Override
	public void waitUntilOff() throws InterruptedException {
		mutable.waitUntilOff();
	}

	@Override
	public void waitUntilOn() throws InterruptedException {
		mutable.waitUntilOn();
	}

	@Override
	public void waitUntilRunning() throws InterruptedException {
		mutable.waitUntilRunning();
	}
}
