package com.seamfix.changelog.work;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.seamfix.changelog.model.QueryData;
import com.seamfix.changelog.props.PropertiesManager;

@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;
	
	@Inject
	PropertiesManager propertiesManager;

	@Inject
	Logger logger;

	public  String changeLogs(String key) {
		String target =propertiesManager.getProperty("changelogUrl", "https://seamfix.atlassian.net/rest/api/3/issue/" + key +"/changelog?");
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", dataBean.getAuth())
					.get(String.class);
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public JsonArray getStringResponse() {
		String key = dataBean.getTaskID();
		
		if (changeLogs(key) == null) {
			prepareErrorMessage(Status.NOT_FOUND, "Connection Error", "Couldn't connect the the JIRA API");
			return null;
		}
		
		JsonObject root = Json.createReader(new StringReader(changeLogs(key))).readObject();
		
		if (root == null) {
			prepareErrorMessage(Status.FORBIDDEN, "Changelog Error", "Couldn't get changelog");
			return null;
		}
		return root.getJsonArray("values");
	}

	public void setValues() {

		JsonArray values = getStringResponse();
		
		if (values == null) {
			prepareErrorMessage(Status.FORBIDDEN, "Changelog Error", "Couldn't get changelog");
			return;
		}

		List<String> listOfFromString = new ArrayList<>();
		List<String> listOfToString = new ArrayList<>();
		List<JsonObject> filteredValues = values
				.stream()
				.filter(value -> value.asJsonObject().getJsonArray("items").getJsonObject(0).getString("field").equals("status"))
				.map(value -> value.asJsonObject())
				.collect(Collectors.toList());
		
		int i=0;
		if(i == filteredValues.size()) {
			dataBean.setDateCreated("No Time Moved");
			dataBean.setDateModified("No Time Moved");
			dataBean.setCurrentStatus("Closed");

			String fromString ="Open";
			listOfFromString.add(fromString);
			dataBean.setFromString(listOfFromString);

			String toString = "Closed";
			listOfToString.add(toString);
			dataBean.setToString(listOfToString);
		}else {     
			for (int j = 0; j < filteredValues.size(); j++) {
				String createdTime = filteredValues.get(0).getString("created");
				dataBean.setDateCreated(createdTime);

				String modifiedTime = filteredValues.get(filteredValues.size() - 1).getString("created");
				dataBean.setDateModified(modifiedTime);

				JsonObject value = filteredValues.get(j);

				String fromString = value.getJsonArray("items").getJsonObject(0).getString("fromString");
				listOfFromString.add(j,fromString);
				dataBean.setFromString(listOfFromString);

				String toString = value.getJsonArray("items").getJsonObject(0).getString("toString");
				listOfToString.add(j,toString);

				dataBean.setToString(listOfToString);

				if(listOfToString.size() == 1) {
					String currentStatus = listOfToString.get(0);
					dataBean.setCurrentStatus(currentStatus);
				}else {
					String currentStatus = listOfToString.get(listOfToString.size() - 1);
					dataBean.setCurrentStatus(currentStatus);
				}
			}
		}	
	}
	public void getStories(){
		JsonArray values = getStringResponse();
		if (values == null || values.isEmpty()) {
			prepareErrorMessage(Status.FORBIDDEN, "Changelog Error", "Couldn't get stories");
			return;
		}
		List<JsonObject> stories = values
				.stream()
				.filter(story -> story.asJsonObject().getJsonArray("items").getJsonObject(0).getString("field").equals("Story Points"))
				.map(story -> story.asJsonObject())
				.collect(Collectors.toList());
		
		int k =0;
		if(k == stories.size()) {
			dataBean.setStoryPoint("0");
		}else {
			for(JsonObject storyAll: stories) {

				String storyPoint = storyAll.getJsonArray("items").getJsonObject(0).getString("toString");
				dataBean.setStoryPoint(storyPoint);
			}
		}
	}

	public void getJSON() {
		setValues();
		getStories();
	}
	
	private void prepareErrorMessage(Status status, String error, String message) {
		dataBean.setStatus(status);
		dataBean.setError(error);
		dataBean.setMessage(message);
	}
}


