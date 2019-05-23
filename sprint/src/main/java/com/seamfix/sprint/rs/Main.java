package com.seamfix.sprint.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.seamfix.sprint.model.QueryData;
import com.seamfix.sprint.work.Workbook;


@Path("/")
public class Main {


	@Inject
	QueryData dataBean;
	
	@Inject 
	Workbook workbook;
	
	@GET
	@Path("{projectID}/{sprintID}")
	public Response call(@PathParam("projectID") int projectID, @PathParam("sprintID") int sprintID) {
	    dataBean.init(projectID, sprintID );
		workbook.getJSON();

		return Response.ok().entity(dataBean.rsJSON()).type("application/json").build();
	}
}
