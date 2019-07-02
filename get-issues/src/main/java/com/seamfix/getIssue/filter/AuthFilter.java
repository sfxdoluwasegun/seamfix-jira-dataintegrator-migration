package com.seamfix.getIssue.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpHeaders;

import com.seamfix.getIssue.model.QueryData;


@Provider
public class AuthFilter implements ContainerRequestFilter {
	
	
	@Inject
	QueryData dataBean;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String auth = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		dataBean.setAuth(auth);
		
	}

}
