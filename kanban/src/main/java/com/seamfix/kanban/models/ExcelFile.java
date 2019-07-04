package com.seamfix.kanban.models;


import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ExcelFile {
	private String key;
	private String storyPoint;
	private String dateCreated;
	private String dateModified;
	private String assignee;
	private String currentStatus;
	private String worklog;
	private String count;

	private List<String> fromString = new ArrayList<>();
	
}