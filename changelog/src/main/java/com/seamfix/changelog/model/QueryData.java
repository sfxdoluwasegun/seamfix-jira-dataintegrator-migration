package com.seamfix.changelog.model;

import java.io.StringWriter;
import java.util.List;

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

	private String taskID;
    private String storyPoint;
	private String dateCreated;
	private String dateModified;
	private String reporter;
	private String currentStatus;
		
	private List<String> toString;
	private List<String> fromString;

	private StringWriter sWriter = new StringWriter(); 

	public String rsJSON() {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		
		JsonObjectBuilder json = Json.createObjectBuilder()
				.add("Task ID", getTaskID())
				.add("Start Date", getDateCreated())
				.add("End Date", getDateModified())
				.add("Reporter", getReporter())
				.add("Current Status", getCurrentStatus())
				.add("Story Point", getStoryPoint());
		
				JsonArrayBuilder toStringBuilder = Json.createArrayBuilder();
				for(String toString :  getToString()) {
					toStringBuilder.add(toString);
				}

				JsonArrayBuilder fromStringBuilder = Json.createArrayBuilder();
				for(String fromString : getFromString()) {
					fromStringBuilder.add(fromString);
				}

				json.add("fromString",fromStringBuilder);
				json.add("toString", toStringBuilder);
				arrayBuilder.add(json);
		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(arrayBuilder.build());
		}
		return sWriter.toString();
	}
	
	public void init(String taskID) {
		this.taskID = taskID;
	}
}
