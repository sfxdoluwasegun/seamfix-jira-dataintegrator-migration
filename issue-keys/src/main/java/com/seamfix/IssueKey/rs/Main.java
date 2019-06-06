package com.seamfix.IssueKey.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.seamfix.IssueKey.model.QueryData;
import com.seamfix.IssueKey.work.Workbook;

@Path(value="/")
public class Main {

	@Inject
	Workbook workbook;
	
	@Inject
	QueryData dataBean;
	
	@GET
	@Path("/keys")
	public Response call() {
		workbook.getKeys();
		
		return Response.ok().entity(dataBean.getJSON()).type("application/json").build();
	}
}
