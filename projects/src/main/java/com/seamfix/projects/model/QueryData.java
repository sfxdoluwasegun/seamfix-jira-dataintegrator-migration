package com.seamfix.projects.model;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.core.Response.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RequestScoped
public class QueryData {

	private ArrayList<Project> projects = new ArrayList<>();
	private ArrayList<Scrum> scrum = new ArrayList<>();
	private ArrayList<Kanban> kanban = new ArrayList<>();
	
	private int projectSize;
	
	private String auth;
	private String error;
	private String message;

	private StringWriter sWriter = new StringWriter();
	
	private Status status = Status.OK;

	public String toJsonErr() {
		JsonObject json = Json.createObjectBuilder()
				.add("error", getError())
				.add("message", getMessage())
				.build();

		try (JsonWriter writer = Json.createWriter(getSWriter())) {
			writer.write(json);
		}
		return getSWriter().toString();
	}


	public String JSON() {
		JsonObjectBuilder json = Json.createObjectBuilder();

		JsonArrayBuilder projectBuilder = Json.createArrayBuilder();
		for(Project project :  getProjects()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  String.valueOf(project.getProjectID()))
					.add("name", project.getProjectName())
					.add("key",  project.getProjectKey());
			projectBuilder.add(object);
		}


		JsonArrayBuilder kanbanBuilder = Json.createArrayBuilder();
		for(Kanban kanban :  getKanban()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  String.valueOf(kanban.getProjectID()))
					.add("name", kanban.getProjectName())
					.add("key",  kanban.getProjectKey());
			kanbanBuilder.add(object);
		}

		JsonArrayBuilder scrumBuilder = Json.createArrayBuilder();
		for(Scrum scrum :  getScrum()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  String.valueOf(scrum.getProjectID()))
					.add("name", scrum.getProjectName())
					.add("key",  scrum.getProjectKey());
			scrumBuilder.add(object);
		}

		json.add("project", projectBuilder)
		.add("kanban", kanbanBuilder)
		.add("scrum", scrumBuilder);

		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json.build());
		}
		return sWriter.toString();
	}


	public void addProject(Project project) {
		this.projects.add(project);
	}


	public void init(int projectSize) {
		this.projectSize = projectSize;
		
	}
}
