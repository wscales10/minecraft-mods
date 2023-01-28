package com.woodyscales.contextmod.ipc;

public interface IClient {
	void initialize(int serverPort);

	String send(String message) throws SendException;
}
