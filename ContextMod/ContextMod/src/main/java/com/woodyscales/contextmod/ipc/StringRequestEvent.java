package com.woodyscales.contextmod.ipc;

import java.util.EventObject;

public class StringRequestEvent extends EventObject {

	private final String request;

	public StringRequestEvent(Object source, String request) {
		super(source);
		this.request = request;
	}

	public String getRequest() {
		return request;
	}
}
