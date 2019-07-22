package com.seamfix.changelog.rs;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.seamfix.changelog.model.QueryData;
import com.seamfix.changelog.work.Workbook;

@Path("/")
public class Main {

	@Inject
	Logger logger;
	
	@Inject
	QueryData dataBean;
	
	@Inject 
	Workbook workbook;
	
	@POST
	@Path(value="{taskID}")
	public Response call(@PathParam("taskID") String taskID){
		
	    dataBean.init(taskID);
		workbook.getJSON();

		if (dataBean.getStatus().getFamily() != Status.Family.SUCCESSFUL) 
			return Response.status(dataBean.getStatus()).entity(dataBean.toJsonErr()).type("application/json").build();  

		return Response.ok().entity(dataBean.rsJSON()).type("application/json").build();
	}
}
