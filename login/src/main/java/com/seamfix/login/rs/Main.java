package com.seamfix.login.rs;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.seamfix.login.work.Workbook;

@Path(value = "/")
public class Main {
	@Inject
	Workbook workbook;

	@POST	
	public Response Call() {
		return Response.ok("Done").build();

	}

}
