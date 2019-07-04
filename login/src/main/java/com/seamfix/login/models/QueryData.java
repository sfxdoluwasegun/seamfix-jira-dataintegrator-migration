package com.seamfix.login.models;

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
	private String email;
	private String token;
	private String error;
	private String auth;
	private String message;

	
	private StringWriter sWriter = new StringWriter(); 

	private Status status = Status.OK;


	public void init(QueryData user) {
		this.email = user.getEmail();
		this.token = user.getToken();
	}
	
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
	
	public String Auth() {
		JsonObject json = Json.createObjectBuilder()
				.add("auth", getAuth())
				.build();

		try (JsonWriter writer = Json.createWriter(getSWriter())) {
			writer.write(json);
		}
		return getSWriter().toString();
	}
}