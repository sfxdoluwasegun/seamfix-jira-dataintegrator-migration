package com.seamfix.sprints.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.seamfix.sprints.work.Workbook;

@Path("/")
public class Main {

	@Inject
	Workbook workbook;
	
	
	@GET
	public String call() {
		return workbook.sprints();
	}
		
}
