
package com.woodyscales.contextmod.ipc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.woodyscales.contextmod.coroutines.CoroutineRunException;
import com.woodyscales.contextmod.coroutines.Delegates.ProgressUpdateHandler2;
import com.woodyscales.contextmod.coroutines.ProgressUpdate;
import com.woodyscales.contextmod.events.EventServer2;
import com.woodyscales.contextmod.tasks.GenericTask;
import com.woodyscales.contextmod.tasks.Task;

public class Client extends Entity {
	private IClient sender;
	private final int serverPort;
	private final UUID guid;
	private final EventServer2<ConnectionResponseListener> connectionResponseServer = new EventServer2<>();
	private IServer receiver;

	public Client(int serverPort) {
		this.serverPort = serverPort;
		guid = UUID.randomUUID();
	}

	public int getServerPort() {
		return serverPort;
	}
	
	@Override
	public Integer getMyPort() {
		return receiver == null ? null : receiver.getPort();
	}

	@Override
	public void setMyPort(Integer myPort) {
		throw new UnsupportedOperationException();
	}
	
	public UUID getGuid() {
		return guid;
	}

	@Override
	protected String getGuidString() {
		return getGuid().toString();
	}

	public void SendToServer(Message... messages) throws SendException {
		getInfo().throwIfNotRunning("SendToServer");
		Task.run(() -> sendToServer(messages));
	}

	public GenericTask<Packet> SendToServerAwaitResponse(Message... messages) throws SendException {
		getInfo().throwIfNotRunning("SendToServerAwaitResponse");
		return GenericTask.run(() -> sendToServer(messages));
	}

	@Override
	protected Packet HandleReceivedPacket(Packet packet) {
		getInfo().throwIfNotRunning("HandleReceivedPacket");
		return MakePacket(HandleReceivedRequest(packet.getMessages()));
	}

	@Override
	public Object start(Object parameter, ProgressUpdateHandler2 handler) throws CoroutineRunException {
		sender = Manager.CreateClient();
		sender.initialize(getServerPort());
//		Packet portResponse = null;
//
//		while (portResponse == null) {
//			var response = SendToServerCatchSendException();
//
//			if (response instanceof Packet) {
//				portResponse = (Packet) response;
//			} else if (response instanceof Exception) {
//				handler.handle(new ProgressUpdate(this, response));
//			} else {
//				throw new UnsupportedOperationException();
//			}
//		}

		receiver = Manager.CreateServer();
		try {
			receiver.start(0);
		} catch (ServerStartException ex) {
			handler.handle(new ProgressUpdate(this, ex));
		}

		System.out.println("Connecting");
		Packet connectionResponse = null;

		while (connectionResponse == null) {
			var response = SendToServerCatchSendException(new Message("conn"));

			if (response instanceof Packet) {
				connectionResponse = (Packet) response;
			} else if (response instanceof Exception) {
				handler.handle(new ProgressUpdate(this, response));
			} else {
				throw new UnsupportedOperationException();
			}
		}

		System.out.println(connectionResponse);

		System.out.println("Handling Connection Response");

		var event = new ConnectionResponseEvent(this, connectionResponse.getMessages());

		for (ConnectionResponseListener listener : connectionResponseServer.getListeners()) {
			listener.HandleConnectionResponse(event);
		}

		System.out.println("Handled Connection Response");

		receiver.addListener(e -> Receiver_ReceivedRequest(e.getRequest()));
		return null;
	}

	private Packet sendToServer(Message... messages) throws SendException {
		return Methods.sendPacket(sender, MakePacket(Arrays.asList(messages)));
	}

	private Object SendToServerCatchSendException(Message... messages) {
		try {
			return sendToServer(messages);
		} catch (SendException ex) {
			return ex;
		}
	}

	private Packet MakePacket(List<Message> messages) {
		return new Packet(getGuidString(), getMyPort(), messages);
	}
}
