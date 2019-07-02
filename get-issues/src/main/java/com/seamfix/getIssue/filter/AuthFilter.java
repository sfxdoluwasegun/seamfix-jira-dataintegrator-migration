package com.seamfix.getIssue.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Strings;
import com.seamfix.getIssue.model.QueryData;


@Provider
public class AuthFilter implements ContainerRequestFilter {
	
	
	@Inject
	QueryData dataBean;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String auth = requestContext.getHeaderString("authorization");
		if(Strings.isNullOrEmpty(auth)) {
			throw new NotAuthorizedException("No Authorization");
		}
		
		dataBean.setAuth(auth);
	}

}
