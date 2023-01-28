package com.woodyscales.contextmod.ipc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.woodyscales.contextmod.events.EventServer1;

class HttpIpcServer extends EventServer1<StringRequestListener> implements IServer
{
	private HttpServer httpServer;
	private final MyHandler handler = new MyHandler();

	public HttpIpcServer()  {
	}

	@Override
	public StringRequestListener getListener() {
		return handler.getListener();
	}
	
	@Override
	protected void setListener(StringRequestListener listener) {
		handler.setListener(listener);
	}

	@Override
	public void start(int myPort) throws ServerStartException {
		try {
			httpServer = HttpServer.create(new InetSocketAddress(myPort), 0);
		} catch (IOException e) {
			throw new ServerStartException(e);
		}

		httpServer.createContext("/", handler);
		httpServer.setExecutor(null);
		httpServer.start();
	}

	class MyHandler implements HttpHandler
	{

		private StringRequestListener listener;

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
				var requestMessage = read(exchange.getRequestBody());
				var responseMessage = getListener() == null ? null
						: getListener().handle(new StringRequestEvent(this, requestMessage));
				var responseHeaders = exchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "text/plain");
				var bytes = responseMessage.getBytes();
				respond(exchange, bytes);
			}
		}

		private static String read(InputStream stream) throws IOException {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];

			for (int length; (length = stream.read(buffer)) != -1;) {
				result.write(buffer, 0, length);
			}

			// StandardCharsets.UTF_8.name() > JDK 7
			return result.toString("UTF-8");
		}

		private static void respond(HttpExchange exchange, byte[] bytes) throws IOException {
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
			OutputStream os = exchange.getResponseBody();
			os.write(bytes);
			os.close();
		}

		public StringRequestListener getListener() {
			return listener;
		}

		public void setListener(StringRequestListener listener) {
			this.listener = listener;
		}
	}

	@Override
	public Integer getPort() {
		if (httpServer == null) {
			return null;
		}

		var address = httpServer.getAddress();

		if (address == null) {
			return null;
		}

		return address.getPort();
	}
}