package com.woodyscales.contextmod.ipc;

public class Manager {
	public static IClient CreateClient()
	{
		return new HttpIpcClient();
	}

	public static IServer CreateServer()
	{
		return new HttpIpcServer();
	}
}
