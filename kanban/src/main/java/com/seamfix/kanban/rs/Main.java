package com.seamfix.kanban.rs;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.seamfix.kanban.models.QueryData;
import com.seamfix.kanban.work.Workbook;

@Path("")
public class Main {

	@Inject
	Workbook workbook;
	
	@Inject
	QueryData dataBean;
	
	@POST
	@Path("/{kanbanInfo}/{projectName}")
	public Response call(QueryData request, @PathParam("kanbanInfo") String kanban, @PathParam("projectName") String projectName) {
		dataBean.init(request, projectName);

		 workbook.getParentKeys();
		return Response.ok().entity(dataBean.getJSON()).type("application/json").build();
	}
	
	@POST
	@Path("/file/{kanbanInfo}/{projectName}")
	public void callFile(QueryData request, @PathParam("kanbanInfo") String kanban,  @PathParam("projectName") String projectName) {
		dataBean.init(request, projectName);

		 workbook.getAllIssues();
	}
}
