package com.seamfix.sprint.model;

import java.io.StringWriter;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import lombok.Getter;
import lombok.Setter;

@Getter																									
@Setter
@RequestScoped
public class QueryData {

	private int projectID;
	private int sprintID;
    private String totalStoryPoint;
	private String completeStoryPoint;
	private String startDate;
	private String endDate;
	private String auth ;
	
	private StringWriter sWriter = new StringWriter(); 
	
	public String rsJSON() {
		JsonObject json = (JsonObject) Json.createObjectBuilder()
				.add("SprintID", getSprintID())
				.add("Total Story Point", getTotalStoryPoint())
				.add("Complete Story Point", getCompleteStoryPoint())
				.add("Start Date", getStartDate())
				.add("End Date", getEndDate())
				.build();

		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json);
		}
		return sWriter.toString();
	}
	
	public void init(int sprintID, String token) {
		this.sprintID = sprintID;
		this.auth = token;
	}
}
