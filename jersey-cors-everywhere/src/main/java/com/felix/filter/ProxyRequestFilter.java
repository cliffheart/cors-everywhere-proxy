package com.felix.filter;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * Proxy filter
 * @author ttdat
 *
 */
public class ProxyRequestFilter extends ClientFilter {

	@Override
	public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
		ClientHandler handler = getNext();
		ClientResponse response = handler.handle(request);

		if (response.getStatusInfo().getFamily().equals(Response.Status.Family.REDIRECTION)) {
			String redirectTarget = response.getHeaders().getFirst("Location");
			request.setURI(UriBuilder.fromUri(redirectTarget).build());
			return handler.handle(request);
		}
		
		return response;
	}

}
