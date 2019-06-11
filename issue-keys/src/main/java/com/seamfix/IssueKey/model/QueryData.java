package com.seamfix.IssueKey.model;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
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

	private ArrayList<Issues> issues = new ArrayList<>();
	private ArrayList<Parent> parent = new ArrayList<>();
	private ArrayList<ExcelFile> file = new ArrayList<>();
	private ArrayList<TranstitionHistory> history = new ArrayList<>();
	
	private int projectID;
	private int sprintID;
	private long members;
	private double totalPoints;


	private StringWriter sWriter = new StringWriter();

	public String getJSON() {
		JsonObjectBuilder json = Json.createObjectBuilder()
				.add("totalMember", getMembers())
				.add("totalPoints", getTotalPoints());
		JsonArrayBuilder parentBuilder = Json.createArrayBuilder();
		for(Parent parent : getParent()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  parent.getId())
					.add("key", parent.getKey())
					.add("startDate", parent.getDateCreated())
					.add("assignee", parent.getAssignee())
					.add("endDate", parent.getDateModified())
					.add("currentStatus", parent.getCurrentStatus())
					.add("storyPoint", parent.getStoryPoint());
			parentBuilder.add(object);
		}

		JsonArrayBuilder issuesBuilder = Json.createArrayBuilder();
		for(Issues issues : getIssues()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  issues.getId())
					.add("key", issues.getKey())
					.add("assignee", issues.getAssignee());
			issuesBuilder.add(object);
		}

		json.add("parent", parentBuilder);
		json.add("issues", issuesBuilder);


		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json.build());
		}
		return sWriter.toString();

	}

//	public String excelFile() {
//
//		JsonObjectBuilder json = Json.createObjectBuilder();
//		for(File file : getFile()) {
//			JsonObjectBuilder object = Json.createObjectBuilder()
//					.add("key", file.getKey())
//					.add("startDate", file.getDateCreated())
//					.add("assignee", file.getAssignee())
//					.add("endDate", file.getDateModified())
//					.add("currentStatus", file.getCurrentStatus())
//					.add("storyPoint", file.getStoryPoint());
//			
//			JsonArrayBuilder toStringBuilder = Json.createArrayBuilder();
//			
//			for(TranstitionHistory thistory : getHistory()) {
//				JsonObjectBuilder tobject = Json.createObjectBuilder()
//						.add("fromString", (JsonValue) thistory.getFromString())
//						.add("toString", (JsonValue) thistory.getToString());
//				toStringBuilder.add(tobject);
//			}
//			object.add("toString", toStringBuilder);
//								json.add( "issues", object);
//		
//		}
//
//		try (JsonWriter writer = Json.createWriter(sWriter)) {
//			writer.write(json.build());
//		}
//		return sWriter.toString();
//	}

	public void init(int projectID, int sprintID) {

		this.projectID = projectID;
		this.sprintID = sprintID;
	}


}

