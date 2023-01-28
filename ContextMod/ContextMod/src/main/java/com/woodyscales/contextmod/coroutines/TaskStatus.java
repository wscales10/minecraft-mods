package com.woodyscales.contextmod.coroutines;

public enum TaskStatus {
	Created,
	WaitingForActivation,
	WaitingToRun,
	Running,
	WaitingForChildrenToComplete,
	RanToCompletion,
	Canceled,
	Faulted
}
