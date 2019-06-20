package com.seamfix.IssueKey.model;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

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
	private long members;
	private double totalPoints;
	private double completePoints;

	private StringWriter sWriter = new StringWriter();

	public String getJSON() {
		JsonObjectBuilder json = Json.createObjectBuilder()
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

