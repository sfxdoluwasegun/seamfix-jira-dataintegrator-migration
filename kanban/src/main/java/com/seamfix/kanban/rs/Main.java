package com.seamfix.kanban.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
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
	
	@GET
	@Path("/")
	public String call() {
		return  workbook.kanbanIssue();
		
	}
	
	
}
