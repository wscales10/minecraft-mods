package com.woodyscales.contextmod.coroutines;

import com.woodyscales.contextmod.coroutines.Delegates.ProgressUpdateHandler2;

public abstract class CoroutineRunner {
	private final RunStateHolder runStateHolder;

	private final MyLock<CoroutineRunException> lockObject = new MyLock<>();

	private final ReadOnlyRunStateHolder info;

	private Coroutine TryStart;

	private Coroutine TryResume;

	private Coroutine TryStop;

	private Coroutine TryPause;

	protected CoroutineRunner() {
		this(RunState.Off);
	}

	protected CoroutineRunner(RunState initialState) {
		runStateHolder = new RunStateHolder(initialState);
		info = runStateHolder.toReadOnly();

		TryStart = new Coroutine((p, h) -> tryStart(p, h));
		TryResume = new Coroutine((p, h) -> tryResume(p, h));
		TryStop = new Coroutine((p, h) -> tryStop(p, h));
		TryPause = new Coroutine((p, h) -> tryPause(p, h));
	}

	protected CoroutineRunner(RunState initialState, boolean initialiseWithCorrespondingMethod) throws Exception {
		this(initialState);

		if (initialiseWithCorrespondingMethod) {
			CoroutineRun run;
			switch (initialState) {
			case Off:
				run = new Coroutine((p, h) -> Stop(p, h)).CreateRun().RunToCompletion();

				if (!run.getResult().isSuccess()) {
					throw new Exception("stop failed");
				}

				break;

			case Running:
				run = new Coroutine((p, h) -> start(p, h)).CreateRun().RunToCompletion();

				if (!run.getResult().isSuccess()) {
					throw new Exception("start failed");
				}

				break;

			case Paused:
				run = new Coroutine((p, h) -> Pause(p, h)).CreateRun().RunToCompletion();

				if (!run.getResult().isSuccess()) {
					throw new Exception("pause failed");
				}

				break;
			}
		}
	}

	public ReadOnlyRunStateHolder getInfo() {
		return info;
	}

	public Coroutine getTryStart() {
		return TryStart;
	}

	public Coroutine getTryStop() {
		return TryStop;
	}

	public Coroutine getTryResume() {
		return TryResume;
	}

	public Coroutine getTryPause() {
		return TryPause;
	}

	protected Object Resume(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		return start(parameter, handler);
	}

	protected Object Pause(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		return Coroutine.DefaultMethod(parameter, handler);
	}

	protected Object start(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		return Coroutine.DefaultMethod(parameter, handler);
	}

	protected Object Stop(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		return Coroutine.DefaultMethod(parameter, handler);
	}

	private Object tryResume(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		return lockObject.lock(() -> {
			CoroutineRun run = new Coroutine((p, h) -> Resume(p, h)).CreateRun(parameter);

			if (runStateHolder.isPaused()) {
				run.Run(progressUpdate -> {
					try {
						handler.handle(progressUpdate);
						return true;
					} catch (CanceledCoroutineRunException ex) {
						return false;
					}
				});

				Result result = run.getResult();
				if (result.isSuccess()) {
					runStateHolder.setState(RunState.Running);
					return result.getValue();
				}
			}

			throw new FailedCoroutineRunException();
		});
	}

	private Object tryPause(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		return lockObject.lock(() -> {

			CoroutineRun run = new Coroutine((p, h) -> Pause(p, h)).CreateRun(parameter);

			if (runStateHolder.isRunning()) {
				run.Run(progressUpdate -> {
					try {
						handler.handle(progressUpdate);
						return true;
					} catch (CanceledCoroutineRunException ex) {
						return false;
					}
				});

				Result result = run.getResult();
				if (result.isSuccess()) {
					runStateHolder.setState(RunState.Paused);
					return result.getValue();
				}
			}

			throw new FailedCoroutineRunException();
		});
	}

	private Object tryStop(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		return lockObject.lock(() -> {

			CoroutineRun run = new Coroutine((p, h) -> Stop(p, h)).CreateRun(parameter);

			if (runStateHolder.isOn()) {
				run.Run(progressUpdate -> {
					try {
						handler.handle(progressUpdate);
						return true;
					} catch (CanceledCoroutineRunException ex) {
						return false;
					}
				});

				Result result = run.getResult();
				if (result.isSuccess()) {
					runStateHolder.setState(RunState.Off);
					return result.getValue();
				}
			}

			throw new FailedCoroutineRunException();
		});
	}

	private Object tryStart(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		return lockObject.lock(() -> {

			CoroutineRun run = new Coroutine((p, h) -> start(p, h)).CreateRun(parameter);

			if (!runStateHolder.isOn()) {
				run.Run(progressUpdate -> {
					try {
						handler.handle(progressUpdate);
						return true;
					} catch (CanceledCoroutineRunException ex) {
						return false;
					}
				});

				Result result = run.getResult();
				// TODO: maybe cancel should propagate up as cancel, not failure?
				if (result.isSuccess()) {
					runStateHolder.setState(RunState.Running);
					return result.getValue();
				}
			}

			throw new FailedCoroutineRunException();
		});
	}
}
