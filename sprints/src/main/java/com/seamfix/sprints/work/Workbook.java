package com.seamfix.sprints.work;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.persistence.criteria.Root;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;

import com.arjuna.ats.internal.arjuna.objectstore.jdbc.drivers.ibm_driver;
import com.seamfix.sprints.model.QueryData;
import com.seamfix.sprints.model.Project;


@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;

	private static String getAuthHeader() {
		final String email = "mabikoye@seamfix.com";
		final String token= "wXtzMKuBuOmzoRJJrNDtCF23";
		String auth = email +":"+ token;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(Charset.forName("ISO-8859-1")));
		return "Basic " + encodedAuth;
	}


	public  String sprints() {
		String target ="https://seamfix.atlassian.net/rest/agile/1.0/board/" + dataBean.getProjectID() +"/sprint";
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthHeader())
					.get(String.class);
		} finally {
			if (client != null)
				client.close();
		}
	}
	
	public String sprintDetail() {
		String target ="https://seamfix.atlassian.net/rest/agile/1.0/sprint/" + dataBean.getSprintID();
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthHeader())
					.get(String.class);
		} finally {
			if (client != null)
				client.close();
		}
	
	}

	public void getJSON() {
		JsonObject root = Json.createReader(new StringReader(sprints())).readObject();

		JsonArray values = root.getJsonArray("values");


		for(int i = 0; i < values.size(); i++ ) {
			Project project = new Project();
			String name = values.asJsonArray().getJsonObject(i).getString("name");
			project.setName(name);

			int id = values.asJsonArray().getJsonObject(i).getInt("id");
			project.setId(id);			
			String startDate = values.asJsonArray().getJsonObject(i).getString("startDate");
			project.setStartDate(startDate);

			String endDate = values.asJsonArray().getJsonObject(i).getString("endDate");
			project.setEndDate(endDate);
			
			dataBean.getProject().add(project);
		}
	}
	
	public void sprint() {
		JsonObject root = Json.createReader(new StringReader(sprintDetail())).readObject();
		
		int id = root.getInt("id");
		dataBean.setSprintID(id);
		
		String startDate = root.getString("startDate");
		dataBean.setStartDate(startDate);
		
		String endDate = root.getString("endDate");
		dataBean.setEndDate(endDate);
	}

}
