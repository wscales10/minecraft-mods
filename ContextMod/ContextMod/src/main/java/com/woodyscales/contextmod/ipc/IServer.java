package com.woodyscales.contextmod.ipc;

import com.woodyscales.contextmod.events.EventServer;

public interface IServer extends EventServer<StringRequestListener>
{
	void start(int myPort) throws ServerStartException;

	Integer getPort();
}
