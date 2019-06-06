package com.seamfix.IssueKey.model;

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

	private ArrayList<Issues> issues = new ArrayList<>();
	private ArrayList<Parent> parent = new ArrayList<>();

	private int productID;
	private int sprintID;

	private StringWriter sWriter = new StringWriter();

	public String getJSON() {
		JsonObjectBuilder json = Json.createObjectBuilder();

		JsonArrayBuilder parentBuilder = Json.createArrayBuilder();
		for(Parent parent : getParent()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  parent.getId())
					.add("key", parent.getKey());
			parentBuilder.add(object);
		}

		JsonArrayBuilder issuesBuilder = Json.createArrayBuilder();
		for(Issues issues : getIssues()) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  issues.getId())
					.add("key", issues.getKey());
			issuesBuilder.add(object);
		}

		json.add("parent", parentBuilder);
		json.add("issues", issuesBuilder);


		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json.build());
		}
		return sWriter.toString();

	}
}

