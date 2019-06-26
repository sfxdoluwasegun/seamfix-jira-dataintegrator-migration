package com.seamfix.changelog.model;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;

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
	private String auth;
	
	private List<String> toString;
	private List<String> fromString;

	private StringWriter sWriter = new StringWriter(); 

	public String rsJSON() {
		JsonObjectBuilder arrayBuilder = Json.createObjectBuilder();
		
		JsonObjectBuilder json = Json.createObjectBuilder()
				.add("taskID", getTaskID())
				.add("startDate", getDateCreated())
				.add("endDate", getDateModified())
				.add("currentStatus", getCurrentStatus())
				.add("storyPoint", getStoryPoint());
		
		JsonObjectBuilder toStringBuilder = Json.createObjectBuilder();
		
		JsonArrayBuilder fromBuilder = Json.createArrayBuilder();
		for(String fromString : getFromString()) {
			fromBuilder.add(fromString);
		}
		JsonArrayBuilder toBuilder = Json.createArrayBuilder();
		for(String toString :  getToString()) {
			toBuilder.add(toString);
		}
		
		toStringBuilder.add("fromString",fromBuilder);
		toStringBuilder.add("toString",toBuilder);
		
		json.add("flow", toStringBuilder);
		arrayBuilder.add("issues", json);
		
		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(arrayBuilder.build());
		}
		return sWriter.toString();
	}
	
	public void init(QueryData request, String taskID) {
		this.taskID = taskID;
	}
}
