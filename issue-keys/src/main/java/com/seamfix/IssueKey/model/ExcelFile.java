package com.seamfix.IssueKey.model;


import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;

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
    
	private int count;

	private List<String> fromString = new ArrayList<>();
	
}