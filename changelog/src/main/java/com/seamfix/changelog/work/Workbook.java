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
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.jboss.shrinkwrap.descriptor.api.beans11.IfClassAvailable;

import com.seamfix.changelog.model.QueryData;
import com.seamfix.changelog.model.TransitionHistory;

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



		List<String> listOfToString = new ArrayList<>();
		List<String> listOfAuthors = new ArrayList<>();
		List<String> listOfPoints = new ArrayList<>();
		
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

			TransitionHistory histories = new TransitionHistory();

			String fromString = value.getJsonArray("items").getJsonObject(0).getString("fromString");
			histories.setFromString(fromString);

			String toString = value.getJsonArray("items").getJsonObject(0).getString("toString");
			listOfToString.add(toString);
			histories.setToString(toString);

			dataBean.getHistories().add(histories);

			String currentStatus = listOfToString.get(listOfToString.size() - 1);
			dataBean.setCurrentStatus(currentStatus);

			JsonObject createdTimeError = filteredValues.get(0);	
			System.out.println(createdTimeError.containsKey("created") );
			if (createdTimeError.containsKey("created")) {
				String createdTime = filteredValues.get(0).getString("created");
				dataBean.setDateCreated(createdTime);

				String modifiedTime = filteredValues.get(filteredValues.size() - 1).getString("created");
				dataBean.setDateModified(modifiedTime);
			} else {
				dataBean.setDateCreated("No createdtime");
				dataBean.setDateModified("No modified date");
			}


			String reporter = value.getJsonObject("author").getString("displayName");
			listOfAuthors.add(reporter);
			dataBean.setReporter(reporter);

			int k = 0;
			if (k == stories.size()) {
				dataBean.setStoryPoint("No Story Point");
			}else {
				JsonObject storyAll = stories.get(k);
				String storyPoint = storyAll.getJsonArray("items").getJsonObject(0).getString("toString");
				listOfPoints.add(storyPoint);
				dataBean.setStoryPoint(storyPoint);
			}
		}
//		long totalMemeber = listOfAuthors.stream().distinct().count();
//		System.out.println(totalMemeber);
		
		 //int sum = listOfPoints.stream().mapToInt(Integer::intValue).sum();
	}
}


