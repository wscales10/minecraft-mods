package com.woodyscales.contextmod.events;

import java.util.EventListener;

public abstract class EventServerBase<TListener extends EventListener> implements EventServer<TListener> {
	@Override
	public abstract void addListener(TListener listener);

	@Override
	public abstract void removeListener(TListener listener);
}
