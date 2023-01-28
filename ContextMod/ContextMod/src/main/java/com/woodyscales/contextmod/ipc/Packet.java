package com.woodyscales.contextmod.ipc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.Gson;

public class Packet {
	private static final Gson gson = getGson();
	
	private final String guid;
	
	private final Integer port;
	
	private final List<Message> messages;
	
	public Packet(String guid, Integer port, List<Message> messages)
	{
		this.guid = guid;
		this.port = port;
		this.messages = Collections.unmodifiableList(messages);
	}

	private static Gson getGson() {
		return new Gson();
	}

	public String getGuid() {
		return guid;
	}

	public Integer getPort() {
		return port;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public static Packet parseJson(String json)
	{
		return gson.fromJson(json, Packet.class);
	}

	public static Packet parseOldString(String s) throws Exception
	{
		var lines = Methods.split(s);
		List<Message> messages = Arrays.stream(lines).map(l -> Message.parseJson(l)).toList();

		Message guidMessage = null, portMessage = null;
		ArrayList<Message> messagesToKeep = new ArrayList<>();
		
		for(Message message : messages) {
			switch (message.getKey())
			{
				case "guid":
					if (guidMessage == null)
					{
						guidMessage = message;
					}
					else
					{
						throw new Exception("Input string not in the correct format");
					}

				case "port":
					if (portMessage == null)
					{
						portMessage = message;
					}
					else
					{
						throw new Exception("Input string not in the correct format");
					}
				default:
					messagesToKeep.add(message);
			}
		}
		
		return new Packet(guidMessage.getValue(), portMessage == null || portMessage.getValue() == null ? null : Integer.parseInt(portMessage.getValue()), messagesToKeep);
	}

	@Override
	public final String toString() {
		return gson.toJson(this);
	}

	public String asString() {
		var header = new Message[] { new Message("guid", getGuid()), new Message("port", port == null ? null : getPort().toString()) };
		return Methods.join(Stream.concat(Arrays.stream(header), getMessages().stream()).map(m -> m.toString()).toList());
	}
}
