package com.seamfix.sprints.model;

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

	private ArrayList<Sprint> sprints = new ArrayList<>();

	private StringWriter sWriter = new StringWriter(); 

	public String JSON() {


		JsonObjectBuilder json = Json.createObjectBuilder();

		JsonArrayBuilder sprintBuilder = Json.createArrayBuilder();
		for(Sprint sprint : getSprints() ) {
			JsonObjectBuilder object = Json.createObjectBuilder()
					.add("id",  String.valueOf(sprint.getId()))
					.add("name", sprint.getName())
					.add("startDate",  sprint.getStartDate())
					.add("endDate",  sprint.getEndDate());
			sprintBuilder.add(object);
		}

		json.add("sprints", sprintBuilder);


		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json.build());
		}
		return sWriter.toString();

	}

	public void addProject(Sprint sprint) {
		this.sprints.add(sprint);
	}
}
