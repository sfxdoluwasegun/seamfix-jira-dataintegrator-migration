package com.seamfix.IssueKey.model;

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

	private ArrayList<Issues> issues = new ArrayList<>();
	private ArrayList<Parent> parent = new ArrayList<>();
	private ArrayList<ExcelFile> file = new ArrayList<>();

	private int projectID;
	private int sprintID;
	private int sprint;
	
	private String name;
	private String auth;
	private String error;
	private String message;
	private String successMessage;
	
	private long members;
	private long total;
	private double totalPoints;
	private double completePoints;

	private StringWriter sWriter = new StringWriter();
	
	private Status status = Status.OK;
	
	public String success() {
		JsonObject json = Json.createObjectBuilder()
				.add("success", getSuccessMessage())
				.build();

		try (JsonWriter writer = Json.createWriter(getSWriter())) {
			writer.write(json);
		}
		return getSWriter().toString();
	}
	

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
	
	public String getJSON() {
		JsonObjectBuilder json = Json.createObjectBuilder()
				.add("total", getTotal())
				.add("totalMember", getMembers())
				.add("completePoints", getCompletePoints())
				.add("totalPoints", getTotalPoints());

		JsonArrayBuilder storyBuilder = Json.createArrayBuilder();
		for(Parent parent : getParent()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  parent.getId())
					.add("key", parent.getKey())
					.add("assignee", parent.getAssignee())
					.add("storyPoint", parent.getStoryPoint())
					.add("worklog", parent.getWorklog())
					.add("currentStatus", parent.getCurrentStatus())
					.add("dateCreated", parent.getDateCreated())
					.add("dateModified", parent.getDateModified());

			storyBuilder.add(object);
		}

		JsonArrayBuilder issuesBuilder = Json.createArrayBuilder();
		for(Issues issues : getIssues()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  issues.getId())
					.add("key", issues.getKey())
					.add("assignee", issues.getAssignee());
			issuesBuilder.add(object);
		}

		json.add("story", storyBuilder);
		json.add("issues", issuesBuilder);


		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json.build());
		}
		return sWriter.toString();

	}

	public void init(int projectID) {
		this.projectID = projectID;
		
	}


}

