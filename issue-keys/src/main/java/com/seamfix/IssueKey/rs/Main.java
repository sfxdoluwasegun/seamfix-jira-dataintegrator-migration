package com.seamfix.IssueKey.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.seamfix.IssueKey.model.QueryData;
import com.seamfix.IssueKey.work.Workbook;

@Path(value="/")
public class Main {

	@Inject
	Workbook workbook;
	
	@Inject
	QueryData dataBean;
	
	@GET
	@Path("/{projectid}")
	public Response call(@PathParam("projectid") int projectID) {
		 dataBean.init( projectID);
		 workbook.getParentKeys();
		
			if (dataBean.getStatus().getFamily() != Status.Family.SUCCESSFUL) 
				return Response.status(dataBean.getStatus()).entity(dataBean.toJsonErr()).type("application/json").build();  
			
		return Response.ok().entity(dataBean.getJSON()).type("application/json").build();
	}
	
	@GET
	@Path("/file/{projectid}")
	public Response callFile (@PathParam("projectid") int projectID) {
		 dataBean.init( projectID);
		 workbook.getAllIssues();
		 
			if (dataBean.getStatus().getFamily() != Status.Family.SUCCESSFUL) 
				return Response.status(dataBean.getStatus()).entity(dataBean.toJsonErr()).type("application/json").build();  
			
			return null;
		
	}
}
