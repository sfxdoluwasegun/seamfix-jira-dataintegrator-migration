package com.seamfix.sprints.work;

import java.io.StringReader;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;

import com.seamfix.sprints.model.Project;
import com.seamfix.sprints.model.QueryData;


@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;

	public  String sprints() {
		String target ="https://seamfix.atlassian.net/rest/agile/1.0/board/" + dataBean.getProjectID() +"/sprint/";
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION,  dataBean.getAuth())
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
					.header(HttpHeaders.AUTHORIZATION, dataBean.getAuth())
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
			
			if(!values.asJsonArray().getJsonObject(i).containsKey("startDate")) {
				project.setStartDate("No Start Date");
				project.setEndDate("No End Date");
			}else{
			String startDate = values.asJsonArray().getJsonObject(i).getString("startDate");
			project.setStartDate(startDate);

			String endDate = values.asJsonArray().getJsonObject(i).getString("endDate");
			project.setEndDate(endDate);
			}
			dataBean.getProject().add(project);
		}
	}
	
	public void sprint() {
		JsonObject root = Json.createReader(new StringReader(sprintDetail())).readObject();
		
		int id = root.getInt("id");
		dataBean.setSprintID(id);
		
		if(!root.containsKey("startDate")) {
			dataBean.setStartDate("No Start Date");
			dataBean.setEndDate("No End Date");
		}else {
		String startDate = root.getString("startDate");
		dataBean.setStartDate(startDate);
		
		String endDate = root.getString("endDate");
		dataBean.setEndDate(endDate);
		}
	}

}
