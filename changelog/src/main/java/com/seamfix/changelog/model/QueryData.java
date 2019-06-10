package com.seamfix.changelog.model;

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

	private String taskID;
    private String storyPoint;
	private String dateCreated;
	private String dateModified;
	private String reporter;
	private String currentStatus;
		
    private ArrayList<TransitionHistory> histories = new ArrayList<>();

	private StringWriter sWriter = new StringWriter(); 

	public String rsJSON() {
		
		JsonObjectBuilder json = Json.createObjectBuilder()
				.add("taskID", getTaskID())
				.add("startDate", getDateCreated())
				.add("endDate", getDateModified())
				.add("currentStatus", getCurrentStatus())
				.add("storyPoint", getStoryPoint());
		
				JsonArrayBuilder historiesBuilder = Json.createArrayBuilder();
				for(TransitionHistory histories : getHistories()) {
					JsonObjectBuilder object = Json.createObjectBuilder()
							.add("fromString",  histories.getFromString())
							.add("toString", histories.getToString());
					historiesBuilder.add(object);
				}
				json.add("transitionHistory", historiesBuilder);
		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json.build());
		}
		return sWriter.toString();
	}
	
	public void init(String taskID) {
		this.taskID = taskID;
	}
}
