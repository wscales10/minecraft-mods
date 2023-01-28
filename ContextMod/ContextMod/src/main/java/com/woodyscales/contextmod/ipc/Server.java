package com.woodyscales.contextmod.ipc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.woodyscales.contextmod.coroutines.CanceledCoroutineRunException;
import com.woodyscales.contextmod.coroutines.Delegates.ProgressUpdateHandler2;
import com.woodyscales.contextmod.coroutines.ProgressUpdate;
import com.woodyscales.contextmod.events.EventServer1;
import com.woodyscales.contextmod.tasks.Task;

public class Server extends Entity {

	private final Map<String, Integer> strikes = new HashMap<String, Integer>();

	private final PortHelper portHelper = new PortHelper();

	private final Map<String, IClient> clients = new HashMap<String, IClient>();

	private final EventServer1<ClientAddedListener> clientAddedServer = new EventServer1<>();

	public Server(int port) {
		setMyPort(port);
	}

	public List<Message> AddClient(String guid) {
		getInfo().throwIfNotRunning("AddClient");

		var client = Manager.CreateClient();
		clients.put(guid, client);
		strikes.put(guid, 0);
		client.initialize(portHelper.GetPort(guid));
		ClientAddedListener onAddClient = clientAddedServer.getListener();
		var output = onAddClient == null ? null : onAddClient.getMessages(guid);
		return output == null ? Collections.emptyList() : output;
	}

	public void Broadcast(Message... messages) {
		getInfo().throwIfNotRunning("Broadcast");
		var guids = clients.keySet();

		for (var guid : guids) {
			sendToClient(guid, messages);
		}
	}

	public void SendToClient(String guid, Message... messages) {
		getInfo().throwIfNotRunning("SendToClient");
		sendToClient(guid, messages);
	}

	@Override
	protected Object start(Object parameter, ProgressUpdateHandler2 handler) throws CanceledCoroutineRunException {
		var receiver = Manager.CreateServer();
		receiver.addListener(event -> Receiver_ReceivedRequest(event.getRequest()));

		try {
			receiver.start(getMyPort());
		} catch (ServerStartException e) {
			handler.handle(new ProgressUpdate(this, e));
		}

		return null;
	}

	@Override
	protected Packet HandleReceivedPacket(Packet packet) {
		getInfo().throwIfNotRunning("HandleReceivedPacket");

		List<Message> response = new ArrayList<>();
		if (!portHelper.IsPortDefined(packet.getGuid())) {
			Integer port;
			if ((port = packet.getPort()) != null) {
				portHelper.SetPort(packet.getGuid(), port);
				response.addAll(AddClient(packet.getGuid()));
			} else {
				throw new IllegalStateException();
			}
		} else if (!clients.containsKey(packet.getGuid())) {
			response.addAll(AddClient(packet.getGuid()));
		}

		Packet output = MakePacket(packet.getGuid(),
				Stream.concat(response.stream(), HandleReceivedRequest(packet.getMessages()).stream()).toList());
		return output;
	}

	private void sendToClient(String guid, Message... messages) {
		Task.run(() -> {
			var client = clients.get(guid);

			try {
				Methods.sendPacket(client, MakePacket(guid, Arrays.asList(messages)));
			} catch (SendException ex) {
				ex.printStackTrace();
				AddStrike(guid);
			} catch (Exception ex) {
				throw ex;
			}
		});
	}

	private void AddStrike(String guid) {
		var numStrikes = strikes.get(guid) + 1;
		strikes.put(guid, numStrikes);

		if (numStrikes > 2) {
			clients.remove(guid);
			portHelper.Reset(guid);
			strikes.remove(guid);
		}
	}

	private Packet MakePacket(String guid, List<Message> messages) {
		return new Packet(guid, portHelper.GetPort(guid), messages);
	}

	private final class PortHelper {
		private final Map<String, Integer> ports = new HashMap<String, Integer>();

		public boolean IsPortDefined(String guid) {
			return ports.containsKey(guid);
		}

		public int GetPort(String guid) {
			return ports.get(guid);
		}

		public boolean SetPort(String guid, int port) {
			if (ports.entrySet().stream().anyMatch(entry -> entry.getValue() == port && entry.getKey() != guid)) {
				return false;
			}

			ports.put(guid, port);
			return true;
		}

		public void Reset(String guid) {
			ports.remove(guid);
		}
	}

	@Override
	protected String getGuidString() {
		return null;
	}
}
