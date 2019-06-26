package com.seamfix.sprints.rs;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.seamfix.sprints.model.QueryData;
import com.seamfix.sprints.work.Workbook;

@Path("/")
public class Main {

	@Inject
	Workbook workbook;
	
	@Inject
	QueryData dataBean;
	
	@POST
	@Path("/project/{projectID}")
	public Response callProject(QueryData request, @PathParam("projectID") int projectID) {
		 dataBean.init(request, projectID);
		workbook.getJSON();
		
		return Response.ok().entity(dataBean.JSON()).type("application/json").build();
	}
	
	@POST
	@Path("/sprint/{sprintID}")
	public Response callSprint(QueryData request, @PathParam("sprintID") int sprintID) {
		dataBean.initS(request, sprintID);
		workbook.sprint();
		
		return Response.ok().entity(dataBean.getSprintDetail()).type("application/json").build();
	}
}
