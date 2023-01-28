package com.woodyscales.contextmod.ipc;

import com.google.gson.Gson;

public class Message {
	private static final Gson gson = new Gson();
	
	public Message(String key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException();
		}

		this.key = key;
	}
	
	public Message(String key, String value)
	{
		this(key);
		this.value = value;
	}
	
	private String key;
	
	private String value;
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}

	public static Message parseJson(String json)
	{
		return gson.fromJson(json, Message.class);
	}

	public static Message parseOneLineString(String s) throws Exception
	{
		if (s == null)
		{
			throw new IllegalArgumentException();
		}

		int index = s.indexOf(" |");

		if (index == -1)
		{
			if (s.length() < 5)
			{
				return new Message(s, null);
			}
		}
		else if (index < 5)
		{
			if (s.length() < 5)
			{
				return new Message(s.substring(0, index), null);
			}
			else if (s.charAt(6) == ' ')
			{
				return new Message(s.substring(0, index), s.substring(7));
			}
		}

		throw new Exception("Input string is not in correct format");
	}

	public String toOneLineString()
	{
		return getValue() == null ? getKey() : String.format("%s | %s", getKey(), getValue());
	}

	@Override
	public final String toString() {
		return gson.toJson(this);
	}
}
