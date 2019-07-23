package com.seamfix.projects.rs;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.seamfix.projects.model.QueryData;
import com.seamfix.projects.work.Workbook;

@Path("/")
public class Main {

	@Inject
	Logger log;
	
	@Inject
	Workbook workbook;
	
	@Inject
	QueryData dataBean;

	@GET
	@Path(value = "{projectSize}")

	public Response call(@PathParam("projectSize") int projectSize) {
		dataBean.init(projectSize);
		workbook.getJSON();

		if (dataBean.getStatus().getFamily() != Status.Family.SUCCESSFUL) {
			return Response.status(dataBean.getStatus()).entity(dataBean.toJsonErr()).type("application/json").build();  
		}
		return Response.ok().entity(dataBean.JSON()).type("application/json").build();
	}
		
}
