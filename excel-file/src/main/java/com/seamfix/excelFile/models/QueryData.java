package com.seamfix.excelFile.models;

import javax.enterprise.context.RequestScoped;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RequestScoped
public class QueryData {

	private int projectID;
	private int sprintID;

	public void init( int sprintID) {
		this.sprintID = sprintID;
	}

}
