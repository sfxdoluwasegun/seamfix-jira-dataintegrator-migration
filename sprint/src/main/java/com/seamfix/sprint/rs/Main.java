package com.seamfix.sprint.rs;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.seamfix.sprint.model.QueryData;
import com.seamfix.sprint.work.Workbook;


@Path("/")
public class Main {
	
	private final Logger log = Logger.getLogger(Main.class.getName());
	
	@Context UriInfo uriInfo;

	@Inject
	QueryData dataBean;
	
	@Inject 
	Workbook workbook;
	
	@PostConstruct
	public void init() {
		log.log(Level.INFO, "request URI: {0}", uriInfo.getRequestUri());
	}
	
	@GET
	@Path("{sprintID}")
	public Response call( @PathParam("sprintID") int sprintID, @HeaderParam(value = "Authorization") String token) {
	    dataBean.init( sprintID, token);
		workbook.getJSON();

		return Response.ok().entity(dataBean.rsJSON()).type("application/json").build();
	}
}
