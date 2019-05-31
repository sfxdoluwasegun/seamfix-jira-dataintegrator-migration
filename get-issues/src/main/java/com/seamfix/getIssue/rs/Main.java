package com.seamfix.getIssue.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.seamfix.getIssue.model.QueryData;
import com.seamfix.getIssue.work.Workbook;

@Path("/")
public class Main {
	
	@Inject
	QueryData dataBean;
	
	@Inject 
	Workbook workbook;

	@GET
	@Path(value = "{taskID}")

	public Response call(@PathParam("taskID") String taskID) {
	    dataBean.init(taskID);
		workbook.getJSON();

		return Response.ok().entity(dataBean.rsJSON()).type("application/json").build();
	}
		

}
