package com.seamfix.sprints.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
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
	
	/**
	 * This method is to get the list of sprints in a project.
	 * @param projectID for the select project
	 * @return list of sprints in a project
	 */
	@GET
	@Path("/project/{projectID}")
	public Response callProject(@PathParam("projectID") int projectID) {
		 dataBean.init(projectID);
		workbook.getJSON();
		
		return Response.ok().entity(dataBean.JSON()).type("application/json").build();
	}
	
	/**
	 * This method is to get the generated required values.
	 * @param sprintID for the selected sprint
	 * @return list of required values e.g total story points.
	 */
	@GET
	@Path("/sprint/{sprintID}")
	public Response callSprint(@PathParam("sprintID") int sprintID) {
		dataBean.initS(sprintID);
		workbook.sprint();
		
		return Response.ok().entity(dataBean.getSprintDetail()).type("application/json").build();
	}
}
