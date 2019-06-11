package com.seamfix.changelog.model;

import java.io.StringWriter;
import java.util.ArrayList;
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

		
    private ArrayList<TransitionHistory> histories = new ArrayList<>();

	private StringWriter sWriter = new StringWriter(); 

	public String rsJSON() {
		JsonObjectBuilder arrayBuilder = Json.createObjectBuilder();
		
		JsonObjectBuilder json = Json.createObjectBuilder()
				.add("taskID", getTaskID())
				.add("startDate", getDateCreated())
				.add("endDate", getDateModified())
				.add("currentStatus", getCurrentStatus())
				.add("storyPoint", getStoryPoint());
		
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
		arrayBuilder.add("issues", json);
		
		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(arrayBuilder.build());
		}
		return sWriter.toString();
	}
	
	public void init(String taskID) {
		this.taskID = taskID;
	}
}
