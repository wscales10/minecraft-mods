package com.woodyscales.contextmod;

import java.util.EventListener;

public interface ContextListener extends EventListener {
	void contextUpdated(ContextEvent.Update event);
}
