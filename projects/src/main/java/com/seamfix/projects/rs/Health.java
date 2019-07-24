package com.seamfix.projects.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class Health {
	
	@GET
	@Path(value = "/Health")

	public Response call() {
		
		return Response.ok().build();
	}
		

}
