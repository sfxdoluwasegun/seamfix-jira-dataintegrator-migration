package com.seamfix.getIssue.model;

import java.io.StringWriter;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.core.Response.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RequestScoped
public class QueryData {
	
	private String taskID;
	private String worklog;
	private String auth;
	private String error;
	private String message;
	
	private StringWriter sWriter = new StringWriter(); 
	
	private Status status = Status.OK;

	public String toJsonErr() {
		JsonObject json = Json.createObjectBuilder()
				.add("error", getError())
				.add("message", getMessage())
				.build();

		try (JsonWriter writer = Json.createWriter(getSWriter())) {
			writer.write(json);
		}
		return getSWriter().toString();
	}
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
