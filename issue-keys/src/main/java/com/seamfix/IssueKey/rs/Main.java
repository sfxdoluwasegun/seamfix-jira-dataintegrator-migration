package com.seamfix.IssueKey.rs;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.seamfix.IssueKey.model.QueryData;
import com.seamfix.IssueKey.work.Workbook;

@Path(value="/")
public class Main {

	@Inject
	Workbook workbook;
	
	@Inject
	QueryData dataBean;
	
	@POST
	@Path("/{projectID}")
	public Response call(QueryData request, @PathParam("projectID") int projectID) {
		 dataBean.init( request, projectID);
		 workbook.getParentKeys();
		
		return Response.ok().entity(dataBean.getJSON()).type("application/json").build();
	}
	
	@POST
	@Path("/file/{projectID}")
	public void callFile(QueryData request, @PathParam("projectID") int projectID) {
		 dataBean.init(request, projectID);
		 workbook.getAllIssues();
		
	}
}
