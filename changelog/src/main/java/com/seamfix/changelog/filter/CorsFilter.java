package com.seamfix.changelog.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpHeaders;

@Provider
public class CorsFilter implements ContainerResponseFilter{

	@Override
    public void filter(ContainerRequestContext requestContext, 
      ContainerResponseContext responseContext) throws IOException {
          responseContext.getHeaders().add(
           "Access-Control-Allow-Headers",HttpHeaders.AUTHORIZATION);
    }
}

