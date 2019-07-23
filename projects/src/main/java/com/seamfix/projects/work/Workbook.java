package com.seamfix.projects.work;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.seamfix.projects.model.Kanban;
import com.seamfix.projects.model.Project;
import com.seamfix.projects.model.QueryData;
import com.seamfix.projects.model.Scrum;
import com.seamfix.projects.props.PropertiesManager;


@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;

	@Inject
	PropertiesManager propertiesManager;
	
	public String recieveResponse(String target,String key, String jsonRequest) throws BadRequestException, ServiceUnavailableException, WebApplicationException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim()).request()
					.post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON), String.class);
		} finally {
			if (client != null)
				client.close();
		}
	}

	public  String projects() {
		String target =propertiesManager.getProperty("projectsUrl","https://seamfix.atlassian.net/rest/agile/1.0/board?startAt="+dataBean.getProjectSize());
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", dataBean.getAuth())
					.get(String.class);
		} finally {
			if (client != null)
				client.close();
		}
	}

	public JsonArray getStringResponse(){
		if (projects() == null) {
			prepareErrorMessage(Status.NOT_FOUND, "Connection Error", "Couldn't connect to JIRA API");
			return null;
		}
		JsonObject root = Json.createReader(new StringReader(projects())).readObject();
		if (root == null ) {
			prepareErrorMessage(Status.FORBIDDEN, "Project Error", "Empty. Please try again");
			return null ;
		}

		return root.getJsonArray("values");
	}	

	public void getKanbanProjects() {
		JsonArray values = getStringResponse();
		
		if (values == null || values.isEmpty()) {
			prepareErrorMessage(Status.FORBIDDEN, "Kanbans Error", "This isn't kanban");
			return ;
		}
		
		List<JsonObject> kanbans = values
				.stream()
				.filter(kandan -> kandan.asJsonObject().getString("type").equals("kanban"))
				.map(kandan -> kandan.asJsonObject())
				.collect(Collectors.toList());	
		
		if (kanbans == null || kanbans.isEmpty()) {
			prepareErrorMessage(Status.FORBIDDEN, "Kanbans Error", "This isn't kanban");
			return ;
		}
		
		for(JsonObject value: kanbans) {

			Project project = new Project();
			Kanban kanban = new Kanban();

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

			dataBean.getProjects().add(project);
			dataBean.getKanban().add(kanban);

		}
	}

	public void getScrumProjects() {
		JsonArray values = getStringResponse();
		

		if (values == null || values.isEmpty()) {
			prepareErrorMessage(Status.FORBIDDEN, "Kanbans Error", "This isn't kanban");
			return ;
		}
		

		List<JsonObject> scrums = values
				.stream()
				.filter(scrum -> scrum.asJsonObject().getString("type").equals("scrum"))
				.map(scrum -> scrum.asJsonObject())
				.collect(Collectors.toList());
		
		if (scrums == null || scrums.isEmpty()) {
			prepareErrorMessage(Status.FORBIDDEN, "Scrums Error", "This isn't scrum");
			return ;
		}

		for(JsonObject value: scrums) {

			Project project = new Project();
			Scrum scrum = new Scrum();

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

	public  void getJSON(){
		getKanbanProjects();
		getScrumProjects();
	}
	
	private void prepareErrorMessage(Status status, String error, String message) {
		dataBean.setStatus(status);
		dataBean.setError(error);
		dataBean.setMessage(message);
	}
}
