package com.seamfix.getIssue.model;

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

	private String taskID;
	private String worklog;
		

	private StringWriter sWriter = new StringWriter(); 

	public String rsJSON() {
		JsonObject json = (JsonObject) Json.createObjectBuilder()
				.add("Task ID", getTaskID())
				.add("Worklog", getWorklog())
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
