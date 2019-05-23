package com.seamfix.changelog.model;

import java.io.StringWriter;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.ws.rs.core.Response.Status;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@RequestScoped
public class QueryData {

	private String taskID;
    private String storyPoint;
	private String dateCreated;
	private String dateModified;
	private String reporter;
	private String currentStatus;
		
	private String error;
	private String message;
	
	private List<String> toString;
	private List<String> fromString;

	private StringWriter sWriter = new StringWriter(); 


	public String rsJSON() {
		JsonObject json = (JsonObject) Json.createObjectBuilder()
				.add("Task ID", getTaskID())
				.add("Start Date", getDateCreated())
				.add("End Date", getDateModified())
				.add("Reporter", getReporter())
				.add("Current Status", getCurrentStatus())
				.add("Story Point", getStoryPoint())
				.add("toString", (JsonValue) getToString())
				.add("fromString",  (JsonValue) getFromString())
				.build();

		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json);
		}
		return sWriter.toString();
	}
	
	public void init(String taskID) {
		this.taskID = taskID;
	}
}
