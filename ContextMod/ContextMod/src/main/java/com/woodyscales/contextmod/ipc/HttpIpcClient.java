package com.woodyscales.contextmod.ipc;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

class HttpIpcClient implements IClient
{
	private final HttpClient httpClient = HttpClient.newHttpClient();
	private URI uri;

	@Override
	public void initialize(int serverPort) {
		this.uri = URI.create(String.format("http://localhost:%s/", serverPort));
	}

	@Override
	public String send(String message) throws SendException {
		var response = postMessage(message);
		return response.body();
	}

	private HttpResponse<String> postMessage(String message) throws SendException
	{
		try
		{
			var request = HttpRequest.newBuilder()
			.uri(uri)
			.POST(HttpRequest.BodyPublishers.ofString(message))
			.build();
			return httpClient.send(request, BodyHandlers.ofString());
		}
		catch (IOException | InterruptedException ex)
		{
			throw new SendException(ex);
		}
	}
}
