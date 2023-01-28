package com.woodyscales.contextmod.events;

import java.util.EventListener;

public interface EventServer<TListener extends EventListener> {
	void addListener(TListener listener);

	void removeListener(TListener listener);
}
