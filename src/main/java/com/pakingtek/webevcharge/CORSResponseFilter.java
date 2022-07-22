package com.pakingtek.webevcharge;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class CORSResponseFilter
implements ContainerRequestFilter,ContainerResponseFilter{
//*
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {

		if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")){
	         // don't do anything if origin is null, its an OPTIONS request, or cors.failure is set
			return;
	    }
		
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();

		headers.add("Access-Control-Allow-Origin", "*");
		//headers.add("Access-Control-Allow-Origin", "http://podcastpedia.org"); //allows CORS requests only coming from podcastpedia.org		
		headers.add("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
		headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia");
	}	//*/
	
	public void filter(ContainerRequestContext requestContext) throws IOException{
		if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")){
			return;
	    }
	}

}