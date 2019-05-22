package com.seamfix.changelog.work;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.seamfix.changelog.model.QueryData;

@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;

	private static String getAuthHeader() {
		final String email = "mabikoye@seamfix.com";
		final String token= "wXtzMKuBuOmzoRJJrNDtCF23";
		String auth = email +":"+ token;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(Charset.forName("ISO-8859-1")));
		return "Basic " + encodedAuth;
	}


	public  String changeLogs(String key) {
		String target ="https://seamfix.atlassian.net/rest/api/3/issue/" + key +"/changelog?";
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthHeader())
					.get(String.class);
		} finally {
			if (client != null)
				client.close();
		}
	}


	public  void getJSON(){
		String key = dataBean.getTaskID();
		JsonObject root = Json.createReader(new StringReader(changeLogs(key))).readObject();



		List<String> listOfFromString = new ArrayList<>();
		List<String> listOfToString = new ArrayList<>();

		JsonArray values = root.getJsonArray("values");

		List<JsonObject> filteredValues = values
				.stream()
				.filter(value -> value.asJsonObject().getJsonArray("items").getJsonObject(0).getString("field").equals("status"))
				.map(value -> value.asJsonObject())
				.collect(Collectors.toList());

		List<JsonObject> stories = values
				.stream()
				.filter(story -> story.asJsonObject().getJsonArray("items").getJsonObject(0).getString("field").equals("Story Points"))
				.map(story -> story.asJsonObject())
				.collect(Collectors.toList());

		for (int j = 0; j < filteredValues.size(); j++) {

			JsonObject value = filteredValues.get(j);

			String fromString = value.getJsonArray("items").getJsonObject(0).getString("fromString");
			listOfFromString.add(j, fromString);

			String toString = value.getJsonArray("items").getJsonObject(0).getString("toString");
			listOfToString.add(j, toString);

			String currentStatus = listOfToString.get(listOfToString.size() - 1);

			String createdTime = filteredValues.get(0).getString("created");

			String modifiedTime = filteredValues.get(filteredValues.size() - 1).getString("created");

			String reporter = value.getJsonObject("author").getString("displayName");
			
			dataBean.setReporter(reporter);
			dataBean.setFromString(listOfFromString);
			dataBean.setToString(listOfToString);
			dataBean.setDateCreated(createdTime);
			dataBean.setCurrentStatus(currentStatus);
			dataBean.setDateModified(modifiedTime);

		}
		for (int k = 0; k < stories.size(); k++) {
			JsonObject storyAll = stories.get(k);
			String storyPoint = storyAll.getJsonArray("items").getJsonObject(0).getString("toString");
			dataBean.setStoryPoint(storyPoint);
		}
	}
}


