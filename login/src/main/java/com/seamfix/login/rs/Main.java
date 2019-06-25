package com.seamfix.login.rs;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.seamfix.login.models.QueryData;
import com.seamfix.login.work.Workbook;

@Path(value = "/")
public class Main {
	@Inject
	Workbook workbook;

	@Inject
	QueryData dataBean;

	@POST
	@Path("/{login}")
	public Response Call(QueryData user, @PathParam("login") String authenticate) {
		dataBean.init(user);
		workbook.check();	

		return Response.ok().entity(dataBean.Auth()).type("application/json").build();
	}
}

