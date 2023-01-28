package com.woodyscales.contextmod.ipc;

import java.util.EventListener;
import java.util.List;

public interface RequestReceivedListener extends EventListener {
	List<Message> requestReceived(RequestReceivedEvent event);
}
