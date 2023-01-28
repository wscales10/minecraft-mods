package com.woodyscales.contextmod.ipc;

import java.util.EventListener;
import java.util.List;

public interface ClientAddedListener extends EventListener {
	List<Message> getMessages(String guid);
}
