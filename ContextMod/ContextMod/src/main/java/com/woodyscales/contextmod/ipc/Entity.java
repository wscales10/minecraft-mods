package com.woodyscales.contextmod.ipc;

import java.util.Collections;
import java.util.List;

import com.woodyscales.contextmod.coroutines.CoroutineRunner;
import com.woodyscales.contextmod.events.EventServer1;

public abstract class Entity extends CoroutineRunner {
	protected Entity() {
		super();
	}

	private EventServer1<RequestReceivedListener> requestReceivedServer = new EventServer1<>();

	private Integer myPort;

	public Integer getMyPort() {
		return myPort;
	}

	protected void setMyPort(Integer myPort) {
		this.myPort = myPort;
	}

	protected abstract String getGuidString();

	protected abstract Packet HandleReceivedPacket(Packet packet);

	protected String Receiver_ReceivedRequest(String arg) {
		return HandleReceivedPacket(Packet.parseJson(arg)).toString();
	}

	protected List<Message> HandleReceivedRequest(List<Message> messages) {
		List<Message> output = null;

		RequestReceivedListener listener = requestReceivedServer.getListener();
		if (listener != null) {
			output = listener.requestReceived(new RequestReceivedEvent(this, messages));
		}

		return output == null ? Collections.emptyList() : output;
	}
}