package com.seamfix.projects.work;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;

import com.seamfix.projects.model.Kanban;
import com.seamfix.projects.model.Project;
import com.seamfix.projects.model.QueryData;
import com.seamfix.projects.model.Scrum;


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


	public  String projects() {
		String target ="https://seamfix.atlassian.net/rest/agile/1.0/board?startAt="+dataBean.getProjectSize();
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

	public  void getJSON(){
		JsonObject root = Json.createReader(new StringReader(projects())).readObject();

		JsonArray values = root.getJsonArray("values");


		List<JsonObject> kanbans = values
				.stream()
				.filter(kandan -> kandan.asJsonObject().getString("type").equals("kanban"))
				.map(kandan -> kandan.asJsonObject())
				.collect(Collectors.toList());

		List<JsonObject> scrums = values
				.stream()
				.filter(scrum -> scrum.asJsonObject().getString("type").equals("scrum"))
				.map(scrum -> scrum.asJsonObject())
				.collect(Collectors.toList());

		for(int i=  0; i < kanbans.size(); i++) {

			Project project = new Project();
			Kanban kanban = new Kanban();

			JsonObject value = kanbans.get(i);
			int id = value.asJsonObject().getInt("id");
			project.setProjectID(id);
			kanban.setProjectID(id);
			
			JsonObject location =  value.asJsonObject().getJsonObject("location");

			String projectKey = location.getString("projectKey");
			project.setProjectKey(projectKey);			
			kanban.setProjectKey(projectKey);	
			
			String projectName = location.getString("name");
			project.setProjectName(projectName);			
			kanban.setProjectName(projectName);
			
			System.out.println(project);
			dataBean.getProjects().add(project);
			dataBean.getKanban().add(kanban);

		}

		for(int j=  0; j < scrums.size(); j++) {
			
			Project project = new Project();
			
			Scrum scrum = new Scrum();

			JsonObject value = scrums.get(j);
			int id = value.asJsonObject().getInt("id");
			project.setProjectID(id);
			scrum.setProjectID(id);
			
			JsonObject location =  value.asJsonObject().getJsonObject("location");

			String projectKey = location.getString("projectKey");
			project.setProjectKey(projectKey);
			scrum.setProjectKey(projectKey);
			
			String projectName = location.getString("name");
			project.setProjectName(projectName);
			scrum.setProjectName(projectName);			

			dataBean.getProjects().add( project);
			dataBean.getScrum().add(scrum);

		}
	}
}
