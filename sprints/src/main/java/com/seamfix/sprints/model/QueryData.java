package com.seamfix.sprints.model;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RequestScoped
public class QueryData {

	private ArrayList<Project> project = new ArrayList<>();

	private int  sprints;

	private String startDate;
	private String endDate;
	private String auth;

	private int projectID;
	private int sprintID;

	private StringWriter sWriter = new StringWriter(); 

	public String JSON() {
		JsonObjectBuilder json = Json.createObjectBuilder();

		JsonArrayBuilder sprintBuilder = Json.createArrayBuilder();
		for(Project project : getProject()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("projectId",  String.valueOf(project.getId()))
					.add("name", project.getName())
					.add("startDate",  project.getStartDate())
					.add("endDate",  project.getEndDate());
			sprintBuilder.add(object);
		}

		json.add("sprints", sprintBuilder);


		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json.build());
		}
		return sWriter.toString();

	}

	public void addProject(Project project) {
		this.project.add(project);
	}

	public void init(QueryData request, int projectID) {
		this.projectID = projectID;
		this.auth = request.auth;
	}

	public void initS(QueryData request, int sprintID) {
		this.sprintID = sprintID;
		this.auth = request.auth;
	}


	public String getSprintDetail() {
		JsonObject json = Json.createObjectBuilder()
				.add("sprintId", getSprintID())
				.add("startDate", getStartDate())
				.add("endDate", getEndDate())
				.build();

		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json);
		}
		return sWriter.toString();

	}
}
