package com.seamfix.sprint.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.seamfix.sprint.work.Workbook;


@Path("/")
public class Main {

	@Inject 
	Workbook workbook;
	
	@GET
	@Path(value = "/log")
	public String call() {
		return workbook.getSprint(87, 532);
	}
}
