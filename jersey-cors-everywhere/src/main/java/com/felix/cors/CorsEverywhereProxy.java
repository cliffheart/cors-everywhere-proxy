
package com.felix.cors;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.felix.filter.ProxyRequestFilter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;

/**
 * Cors everywhere proxy
 * @author ttdat
 *
 */
@Path(CorsEverywhereProxy.PROXY_PATH)
public class CorsEverywhereProxy {
	
	public static final String PROXY_PATH = "/proxy";
	
	protected static final String PATH_PATTERN = "{url : .+}";
	
	@GET
	@Path(PATH_PATTERN)
	public Response get(@Context final HttpServletRequest request,
			@Context final HttpHeaders headers,
			String body) {
		// build proxy response
		return buildResponse(request, headers, body, HttpMethod.GET);
	}
	
	@POST
	@Path(PATH_PATTERN)
	public Response post(@Context final HttpServletRequest request,
			@Context final HttpHeaders headers,
			String body) {
		// build proxy response
		return buildResponse(request, headers, body, HttpMethod.POST);
	}
	
	@PUT
	@Path(PATH_PATTERN)
	public Response put(@Context final HttpServletRequest request,
			@Context final HttpHeaders headers,
			String body) {
		// build proxy response
		return buildResponse(request, headers, body, HttpMethod.PUT);
	}
	
	@DELETE
	@Path(PATH_PATTERN)
	public Response delete(@Context final HttpServletRequest request,
			@Context final HttpHeaders headers,
			String body) {
		// build proxy response
		return buildResponse(request, headers, body, HttpMethod.DELETE);
	}
	
	protected Response buildResponse(final HttpServletRequest request,
			HttpHeaders headers,
			String body,
			String method) {
		// get url
		String url = request.getRequestURI().split(PROXY_PATH.concat("/"))[1];

		WebResource.Builder builder = this.createBuilder(url, headers);
		ClientResponse clientRs = null;
		
		if (Objects.isNull(body)) {
			clientRs = builder.method(method, ClientResponse.class);
		} else {
			clientRs = builder.method(method, ClientResponse.class, body);
		}

		// status code
		ResponseBuilder proxyRs = Response.status(clientRs.getStatus());
		
		// headers
		clientRs.getHeaders().entrySet().forEach(entry -> {
			entry.getValue().forEach(value -> {
				proxyRs.header(entry.getKey(), value);
			});
		});
		
		// body
		proxyRs.entity(clientRs.getEntity(new GenericType<String>() { }));
		
		return proxyRs.build();
	}
	
	protected WebResource.Builder createBuilder(String url, HttpHeaders headers) {
		ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
		
        Client client = Client.create(config);
		client.addFilter(new GZIPContentEncodingFilter(false));
		client.setFollowRedirects(true);
		
		WebResource webResource = client.resource(url);
		webResource.addFilter(new ProxyRequestFilter());
		webResource.addFilter(new LoggingFilter(Logger.getAnonymousLogger()));
		
		WebResource.Builder builder = webResource.getRequestBuilder();

		headers.getRequestHeaders().keySet().forEach(key -> {
			List<String> values = headers.getRequestHeaders().get(key);
			builder.header(key, String.join(",", values));
		});
		
		return builder;
	}
}
