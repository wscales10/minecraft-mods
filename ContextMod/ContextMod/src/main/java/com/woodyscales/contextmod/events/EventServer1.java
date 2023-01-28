package com.woodyscales.contextmod.events;

import java.util.EventListener;

public class EventServer1<TListener extends EventListener> extends EventServerBase<TListener> {
	private TListener listener;

	@Override
	public final void addListener(TListener listener) {
		if (this.getListener() == null) {
			this.setListener(listener);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public final void removeListener(TListener listener) {
		if (this.getListener() == listener) {
			this.setListener(null);
		} else {
			throw new IllegalStateException();
		}
	}

	public TListener getListener() {
		return listener;
	}

	protected void setListener(TListener listener) {
		this.listener = listener;
	}
}