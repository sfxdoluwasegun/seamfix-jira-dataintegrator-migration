package com.seamfix.IssueKey.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parent {
	private String id;
	private String key;
	private String storyPoint;
	private String dateCreated;
	private String dateModified;
	private String reporter;
	private String currentStatus;
}
