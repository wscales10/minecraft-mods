package com.woodyscales.contextmod.coroutines;

public class ProgressUpdate
{
	private final Object sender;
	private final Object args;
	
	public ProgressUpdate(Object sender, Object args)
	{
		this.sender = sender;
		this.args = args;
	}

	public Object getSender() {
		return sender;
	}

	public Object getArgs() {
		return args;
	}
}