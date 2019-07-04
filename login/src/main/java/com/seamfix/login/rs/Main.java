package com.seamfix.login.rs;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
		workbook.getRespone();	
		
		if (dataBean.getStatus().getFamily() != Status.Family.SUCCESSFUL) {
			return Response.status(dataBean.getStatus()).entity(dataBean.toJsonErr()).type("application/json").build();
		}
		return Response.ok().entity(dataBean.Auth()).type("application/json").build();
	}
}

