package com.seamfix.projects.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.seamfix.projects.model.QueryData;
import com.seamfix.projects.work.Workbook;

@Path("/")
public class Main {

	@Inject
	Workbook workbook;
	
	@Inject
	QueryData dataBean;

	@GET
	@Path(value = "{projectSize}")

	public Response call(@PathParam("projectSize") int projectSize) {
		dataBean.init(projectSize);
		workbook.getJSON();

		return Response.ok().entity(dataBean.JSON()).type("application/json").build();
	}
		
}
