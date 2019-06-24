package com.seamfix.login.rs;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.seamfix.login.models.QueryData;
import com.seamfix.login.work.Workbook;

@Path(value = "/")
public class Main {
	@Inject
	Workbook workbook;
	
	@Inject
	QueryData dataBean;

	@POST
	@Path("/{login}")
	public void Call(QueryData user, @PathParam("login") String login) {
		dataBean.init(user);
		workbook.createJson();	
		}

}
