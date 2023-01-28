package com.woodyscales.contextmod.coroutines;

public class RunStateHolder implements IRunStateHolder {
	private final ManualResetEvent notRunningEvent = new ManualResetEvent(true);

	private final ManualResetEvent runningEvent = new ManualResetEvent(false);

	private final ManualResetEvent offEvent = new ManualResetEvent(true);

	private final ManualResetEvent onEvent = new ManualResetEvent(false);

	private RunState state;

	private boolean isOn;

	private boolean isRunning;

	public RunStateHolder() {
		this(RunState.Off);
	}

	public RunStateHolder(RunState initialState) {
		setState(initialState);
	}

	@Override
	public boolean isOn() {
		return isOn;
	}

	private void setOn(boolean isOn) {
		if (this.isOn != isOn) {
			if (isOn) {
				// I'm on now!
				onEvent.set();

				// Allow "off" notification
				offEvent.reset();
			} else {
				// I'm off now!
				offEvent.set();

				// Allow "on" notification
				onEvent.reset();
			}
		}

		this.isOn = isOn;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	private void setRunning(boolean isRunning) {
		if (this.isRunning != isRunning) {
			if (isRunning) {
				// I'm running now!
				runningEvent.set();

				// Allow "not running" notifications
				notRunningEvent.reset();
			} else {
				// I'm not running now!
				notRunningEvent.set();

				// Allow "running" notifications
				runningEvent.reset();
			}
		}

		this.isRunning = isRunning;
	}

	@Override
	public boolean isPaused() {
		return getState() == RunState.Paused;
	}

	public ReadOnlyRunStateHolder toReadOnly() {
		return new ReadOnlyRunStateHolder(this);
	}

	@Override
	public void waitUntilNotRunning() throws InterruptedException {
		notRunningEvent.waitOne();
	}

	@Override
	public void waitUntilOff() throws InterruptedException {
		offEvent.waitOne();
	}

	@Override
	public void waitUntilRunning() throws InterruptedException {
		runningEvent.waitOne();
	}

	@Override
	public void waitUntilOn() throws InterruptedException {
		onEvent.waitOne();
	}

	@Override
	public void throwIfOff(String caller) {
		if (!isOn()) {
			throw new UnsupportedOperationException(
					String.format("This %s does not allow %s while off.", getClass().getSimpleName(), caller));
		}
	}

	@Override
	public void throwIfNotRunning(String caller) {
		if (!isRunning()) {
			throw new UnsupportedOperationException(
					String.format("This %s does not allow %s while not running.", getClass().getSimpleName(), caller));
		}
	}

	@Override
	public RunState getState() {
		return state;
	}

	public void setState(RunState state) {
		switch (this.state = state) {
		case Off:
			setRunning(false);
			setOn(false);
			break;

		case Running:
			setOn(true);
			setRunning(true);
			break;

		case Paused:
			setOn(true);
			setRunning(false);
			break;

		default:
			throw new IllegalArgumentException();
		}
	}
}