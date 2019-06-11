package com.seamfix.excelFile.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.seamfix.excelFile.models.QueryData;
import com.seamfix.excelFile.work.Workbook;

@Path("/file")
public class Main {

	@Inject
	Workbook workbook;

	@Inject
	QueryData dataBean;

	@GET
	@Path("/{sprintID}")
	public void call(@PathParam("sprintID") int sprintID) {
		dataBean.init( sprintID);
		workbook.getFile();
	}
}
