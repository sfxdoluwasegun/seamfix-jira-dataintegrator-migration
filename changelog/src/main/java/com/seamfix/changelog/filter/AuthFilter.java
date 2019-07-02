package com.seamfix.changelog.filter;

import java.io.IOException;
import java.lang.System.Logger;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;


import com.google.common.base.Strings;
import com.seamfix.changelog.model.QueryData;

@Provider
public class AuthFilter implements ContainerRequestFilter {
	
	@Inject
	QueryData dataBean;
	
	@Inject
	Logger log;

	@Override
	public void filter(ContainerRequestContext requestContext) throws WebApplicationException, IOException {
		String auth = requestContext.getHeaderString("authorization");
		if (Strings.isNullOrEmpty(auth)) {
			throw new NotAuthorizedException("No authorization");
		}
		dataBean.setAuth(auth);
	}

}
