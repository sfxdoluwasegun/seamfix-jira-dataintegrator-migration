package com.seamfix.kanban.models;

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
	
	private int projectID;
	private int sprintID;
	private int sprint;
	private String name;
	private long members;
	private double totalPoints;
	private double completePoints;
	

	private ArrayList<Issues> issues = new ArrayList<>();
	private ArrayList<Parent> parent = new ArrayList<>();
	private ArrayList<ExcelFile> file = new ArrayList<>();


}
