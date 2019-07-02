package com.seamfix.sprints.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import com.seamfix.sprints.model.QueryData;

@Provider
public class AuthFilter implements ContainerRequestFilter {
	
	
	@Inject
	QueryData dataBean;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String auth = requestContext.getHeaderString("authorization");
		dataBean.setAuth(auth);
		
	}

}
