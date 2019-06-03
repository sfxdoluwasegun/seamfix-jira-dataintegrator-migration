package com.seamfix.sprints.model;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.ProjectStage;
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
	
	private int projectID;
	private int sprintID;

	private StringWriter sWriter = new StringWriter(); 

	public String JSON() {
		JsonObjectBuilder json = Json.createObjectBuilder();

		JsonArrayBuilder sprintBuilder = Json.createArrayBuilder();
		for(Project sprint : getProject()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("projectId",  String.valueOf(sprint.getId()))
					.add("name", sprint.getName())
					.add("startDate",  sprint.getStartDate())
					.add("endDate",  sprint.getEndDate());
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

	public void init(int projectID) {
		this.projectID = projectID;
		
	}
	
	
}
