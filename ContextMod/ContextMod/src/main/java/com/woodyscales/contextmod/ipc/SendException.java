package com.woodyscales.contextmod.ipc;

public class SendException extends Exception
{
	private static final String message = "Message failed to send.";

	public SendException()
	{
		super(message);
	}

	public SendException(Exception innerException)
	{
		super(message, innerException);
	}
}
