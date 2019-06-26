package com.seamfix.kanban.models;

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

	private int projectID;
	private int sprintID;
	private int sprint;
	private int startAt;
	private int maxResults;

	private String startDate;
	private String endDate;
	private String projectName;
	private String auth;


	private long members;
	private int total;
	private long totalIssue;
	private double totalPoints;
	private double completePoints;


	private ArrayList<Issues> issues = new ArrayList<>();
	private ArrayList<Parent> parent = new ArrayList<>();
	private ArrayList<ExcelFile> file = new ArrayList<>();

	private StringWriter sWriter = new StringWriter();

	public String getJSON() {
		JsonObjectBuilder json = Json.createObjectBuilder()
				.add("total", getTotal())
				.add("totalMember", getMembers())
				.add("completePoints", getCompletePoints())
				.add("totalPoints", getTotalPoints());

		JsonArrayBuilder issuesBuilder = Json.createArrayBuilder();
		for(Issues issues : getIssues()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  issues.getId())
					.add("key", issues.getKey())
					.add("assignee", issues.getAssignee());
			issuesBuilder.add(object);
		}

		json.add("issues", issuesBuilder);


		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json.build());
		}
		return sWriter.toString();

	}

	public void init(QueryData request, String projectName) {
		this.projectName = projectName;
		this.startDate = request.getStartDate();
		this.endDate = request.getEndDate();
		this.maxResults = request.getMaxResults();
		this.startAt = request.getStartAt();
	}

}
