package com.seamfix.projects.model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RequestScoped
public class QueryData {

	private ArrayList<Project> projects = new ArrayList<>();
	private ArrayList<Scrum> scrum = new ArrayList<>();
	private ArrayList<Kanban> kanban = new ArrayList<>();

	private StringWriter sWriter = new StringWriter(); 

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
}
