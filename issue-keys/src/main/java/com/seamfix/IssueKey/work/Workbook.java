package com.seamfix.IssueKey.work;

import java.io.StringReader;
import java.io.StringWriter;
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
import javax.json.JsonWriter;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;

import com.seamfix.IssueKey.model.Issues;
import com.seamfix.IssueKey.model.Parent;
import com.seamfix.IssueKey.model.QueryData;


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


	public  String sprintIssue() {
		String target ="https://seamfix.atlassian.net/rest/agile/1.0/board/87/sprint/532/issue?maxResults=100";
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

	public String createJson(String key) {

		JsonObject json = Json.createObjectBuilder()
				.add("key",key)
				.build();
		StringWriter sWriter = new StringWriter();
		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json);
		}
		return sWriter.toString();
	}

	public String recieveResponse(String key, String jsonRequest) throws BadRequestException, ServiceUnavailableException, WebApplicationException {
		String target = "http://localhost:8088/changelog/"+key;
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim()).request()
					.post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON), String.class);
		} finally {
			if (client != null)
				client.close();
		}
	}

	private JsonObject postService(String key, String json) {

		String response = recieveResponse(key, json);
		return Json.createReader(new StringReader(response)).readObject();
	}

	public void getKeys() {

		JsonObject root = Json.createReader(new StringReader(sprintIssue())).readObject();

		JsonArray issues = root.getJsonArray("issues");

		List<String> listOfAuthors = new ArrayList<>();
		List<Double> listOfPoints = new ArrayList<>();
		
		List<JsonObject> filteredValues = issues
				.stream()
				.filter(issue -> issue.asJsonObject().getString("expand").equals("operations,versionedRepresentations,editmeta,changelog,renderedFields"))
				.map(issue -> issue.asJsonObject())
				.collect(Collectors.toList());

		for(int i =0; i < filteredValues.size(); i++) {
			Issues	issuesq  = new Issues();
			JsonObject issue = filteredValues.get(i);
			String id = issue.getString("id");
			issuesq.setId(id);

			String key = issue.getString("key");
			issuesq.setKey(key);

			String assignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
			issuesq.setAssignee(assignee);
			listOfAuthors.add(assignee);
			
			
			dataBean.getIssues().add(issuesq);

			JsonArray subtasks = issue.getJsonObject("fields").getJsonArray("subtasks");
			if(!subtasks.isEmpty()) {
				
				Parent parent = new Parent();

				String sid = issue.getString("id");
				parent.setId(sid);

				String skey = issue.getString("key");
				parent.setKey(skey);

				String jsonString = createJson(skey);

				JsonObject jsonObject = postService(skey,jsonString);

				if(jsonObject == null) {
					return;
				}

				String startDate = jsonObject.getString("startDate");
				parent.setDateCreated(startDate);

				String endDate = jsonObject.getString("endDate");
				parent.setDateModified(endDate);

				String currentStatus = jsonObject.getString("currentStatus");
				parent.setCurrentStatus(currentStatus);

				String storyPoint = jsonObject.getString("storyPoint");
				parent.setStoryPoint(storyPoint);
				
				Double sum = Double.parseDouble(storyPoint);
				listOfPoints.add(sum);

				String PAssignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
				parent.setAssignee(PAssignee);

				dataBean.getParent().add(parent);
			}
		}
		long totalMemebers = listOfAuthors.stream().distinct().count();	
		dataBean.setMembers(totalMemebers);
		
		Double totalPoints = listOfPoints.stream().mapToDouble(Double::doubleValue).sum();
		dataBean.setTotalPoints(totalPoints);
		
	}

}
