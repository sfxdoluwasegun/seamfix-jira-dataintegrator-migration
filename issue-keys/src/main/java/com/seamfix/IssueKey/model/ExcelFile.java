package com.seamfix.IssueKey.model;

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
}
