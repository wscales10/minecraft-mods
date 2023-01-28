package com.woodyscales.contextmod;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.woodyscales.contextmod.ipc.Message;
import com.woodyscales.contextmod.ipc.Packet;
import com.woodyscales.contextmod.ipc.Server;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("contextmod")
public class ContextMod implements ContextListener {

	private static final Gson gson = getGson();
	private ContextHelper contextHelper = new ContextHelper();
	private Server server;

	public ContextMod() {
		System.out.println(new Packet("guidwuid", 495, List.of(new Message("key1", "value1"), new Message("key2", "value2"))));
		MinecraftForge.EVENT_BUS.register(contextHelper);
		server = new Server(5008);
		
		server.getTryStart().CreateRun().Run(progressUpdate -> {
			return !(progressUpdate.getArgs() instanceof Exception);
		});
		
		contextHelper.addListener(this);
		
		//TODO: pause and resume
	}

	@Override
	public void contextUpdated(ContextEvent.Update event) {
		server.Broadcast(new Message(MinecraftContext.class.getSimpleName(), gson.toJson(event.getContext())));
	}

	private static Gson getGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Type.class, new TypeAdapter<Type>() {

			@Override
			public void write(JsonWriter out, Type value) throws IOException {
				out.value(value.getTypeName());
			}

			@Override
			public Type read(JsonReader in) throws IOException {
				try {
					return Class.forName(in.nextString());
				} catch (ClassNotFoundException e) {
					throw new IOException(e);
				}
			}

		});
		return builder.create();
	}
}
