package com.woodyscales.contextmod.ipc;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;

public class RequestReceivedEvent extends EventObject {

	private List<Message> messages;

	public RequestReceivedEvent(Object source, List<Message> messages) {
		super(source);
		
		this.messages = Collections.unmodifiableList(messages);
	}

	public List<Message> getMessages(){
		return messages;
	}
}
