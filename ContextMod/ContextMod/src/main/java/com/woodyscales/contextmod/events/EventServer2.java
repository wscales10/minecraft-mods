package com.woodyscales.contextmod.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

public class EventServer2<TListener extends EventListener> extends EventServerBase<TListener> {
	private final List<TListener> listeners = new ArrayList<>();
	private final List<TListener> readonlyListeners = Collections.unmodifiableList(listeners);

	@Override
	public final void addListener(TListener listener) {
		listeners.add(listener);
	}

	@Override
	public final void removeListener(TListener listener) {
		listeners.remove(listener);
	}

	public List<TListener> getListeners() {
		return readonlyListeners;
	}
}
