package com.woodyscales.contextmod.ipc;

import java.util.EventListener;

public interface StringRequestListener extends EventListener {
	String handle(StringRequestEvent event);
}
