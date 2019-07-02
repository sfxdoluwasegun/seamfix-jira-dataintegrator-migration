package com.seamfix.kanban.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.seamfix.kanban.models.QueryData;


@Provider
public class AuthFilter implements ContainerRequestFilter {
	
	
	@Inject
	QueryData dataBean;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String auth = requestContext.getHeaderString("authorization");
		if(Strings.isNullOrEmpty(auth)) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		
		dataBean.setAuth(auth);
	}
}
