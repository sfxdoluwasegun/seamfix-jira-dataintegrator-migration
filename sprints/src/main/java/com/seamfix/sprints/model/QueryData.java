package com.seamfix.sprints.model;

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

	private ArrayList<Project> project = new ArrayList<>();

	private int  sprints;

	private String startDate;
	private String endDate;
	private String auth;
	private String error;
	private String message;

	private int projectID;
	private int sprintID;

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

	/**
	 * This method generates the JSON response.
	 * @return JSON response of projectID,name,start date and end date.
	 */
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

/**
 * This method generates the JSON response.
 * @return JSON response of sprintID,start date and end date.
 */
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
	
	public void addProject(Project project) {
		this.project.add(project);
	}

	public void init(int projectID) {
		this.projectID = projectID;
	}

	public void initSprint( int sprintID) {
		this.sprintID = sprintID;
	}

}
