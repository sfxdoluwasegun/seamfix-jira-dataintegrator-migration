package com.seamfix.sprint.rs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("health-check")
public class Health {
	
	@GET
	public Response check() {
		return Response.ok().entity("Test OK").build();
	}

}
