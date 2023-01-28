package com.woodyscales.contextmod.ipc;

public class Methods {
	public static Packet sendPacket(IClient sender, Packet packet) throws SendException
	{
		String outgoing = packet.toString();
		System.out.println("***");
		System.out.println("Outgoing:");
		System.out.println(outgoing);
		var incoming = sender.send(outgoing);
		System.out.println("Incoming:");
		System.out.println(incoming);
		System.out.println("");
		System.out.println("***");
		return Packet.parseJson(incoming);
	}
	
	public static String[] split(String joined)
	{
		return joined.split("\r\n");
	}

	public static String join(Iterable<String> messages)
	{
		return messages == null ? null : String.join("\r\n", messages);
	}
}
