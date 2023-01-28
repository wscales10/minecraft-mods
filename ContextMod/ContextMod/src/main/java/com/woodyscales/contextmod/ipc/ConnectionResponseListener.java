package com.woodyscales.contextmod.ipc;

import java.util.EventListener;

public interface ConnectionResponseListener extends EventListener {
	void HandleConnectionResponse(ConnectionResponseEvent event);
}
