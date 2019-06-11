package com.seamfix.IssueKey.work;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpHeaders;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.infinispan.persistence.modifications.Clear;

import com.fasterxml.jackson.core.sym.Name;
import com.seamfix.IssueKey.model.ExcelFile;
import com.seamfix.IssueKey.model.Issues;
import com.seamfix.IssueKey.model.Parent;
import com.seamfix.IssueKey.model.QueryData;
import com.seamfix.IssueKey.model.TranstitionHistory;


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
		String target ="https://seamfix.atlassian.net/rest/agile/1.0/board/"+dataBean.getProjectID()+"/sprint/"+dataBean.getSprintID()+"/issue?maxResults=100";
		System.out.println(target);
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

	public List<JsonObject> getFilter() {
		JsonObject root = Json.createReader(new StringReader(sprintIssue())).readObject();

		JsonArray issues = root.getJsonArray("issues");


		List<JsonObject> filteredValues = issues
				.stream()
				.filter(issue -> issue.asJsonObject().getString("expand").equals("operations,versionedRepresentations,editmeta,changelog,renderedFields"))
				.map(issue -> issue.asJsonObject())
				.collect(Collectors.toList());

		return filteredValues;
	}
	public void getParentKeys() {
		List<JsonObject> filteredValues = getFilter();

		List<String> listOfAuthors = new ArrayList<>();
		List<Double> listOfPoints = new ArrayList<>();
		List<String> listOfName = new ArrayList<>();
		List<Integer> listOfId = new ArrayList<>();

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

			JsonArray closedSprints = issue.getJsonObject("fields").getJsonArray("closedSprints");
			if(!closedSprints.isEmpty()) {
				if(closedSprints.size() == 1) {
					int sprint = closedSprints.getJsonObject(0).getInt("id");
					System.out.println(sprint);
					String name = closedSprints.getJsonObject(0).getString("name");
					System.out.println(name);
				}
				else {
					for(int k =0; k < closedSprints.size(); k++) {
						int sprint = closedSprints.getJsonObject(closedSprints.size() - 1).getInt("id");
						listOfId.add(sprint);
						System.out.println(listOfId);
						String name = closedSprints.getJsonObject(closedSprints.size() - 1).getString("name");
						listOfName.add(name);
						System.out.println(listOfName);
					}
				}

			}
		}
		long totalMemebers = listOfAuthors.stream().distinct().count();	
		dataBean.setMembers(totalMemebers);

		Double totalPoints = listOfPoints.stream().mapToDouble(Double::doubleValue).sum();
		dataBean.setTotalPoints(totalPoints);

	}

	public void getAllIssues() {
		List<JsonObject> filteredValues = getFilter();
		ExcelFile file = new ExcelFile();
		TranstitionHistory transtitionHistory = new TranstitionHistory();

		BufferedWriter csvWriter = null;
		String path = "C:\\jcodes\\RND\\jira-dataintegrator\\";


		List<String> listOfFromString = new ArrayList<>();
		List<String> listOfToString = new ArrayList<>();

		for(int i = 0; i < filteredValues.size(); i++) {
			JsonObject issue = filteredValues.get(i);

			String key = issue.getString("key");
			file.setKey(key);

			String jsonString = createJson(key);

			JsonObject jsonObject = postService(key,jsonString);

			if(jsonObject == null) {
				return;
			}

			JsonArray histories = jsonObject.getJsonArray("transitionHistory");
			List<JsonObject> tHistories = histories
					.stream()
					.filter(history -> history.asJsonObject().containsKey("fromString"))
					.map(history -> history.asJsonObject())
					.collect(Collectors.toList());

			String startDate = jsonObject.getString("startDate");
			file.setDateCreated(startDate);

			String endDate = jsonObject.getString("endDate");
			file.setDateModified(endDate);

			String currentStatus = jsonObject.getString("currentStatus");
			file.setCurrentStatus(currentStatus);


			String storyPoint = jsonObject.getString("storyPoint");
			file.setStoryPoint(storyPoint);

			String assignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
			file.setAssignee(assignee);
			for(int j = 0; j< tHistories.size(); j++) {
				JsonObject history = tHistories.get(j);
				String fromString = history.getString("fromString");
				listOfFromString.add(fromString);
				transtitionHistory.setFromString(fromString);


				String toString = history.getString("toString");
				listOfToString.add(toString);
				transtitionHistory.setToString(toString);


				dataBean.getHistory().add(transtitionHistory);

				dataBean.getFile().add(file);
			}

			try {
				File csvFile = new File(path +"csvfile.csv");
				csvWriter = new BufferedWriter(new FileWriter(csvFile));
				String line = new StringBuilder()

						.append("Task ID")
						.append(",")
						.append("Start Date")
						.append(",")
						.append("End Date")
						.append(",")
						.append("Story Point")
						.append(",")
						.append("Author")
						.append(",")
						.append("Worklog")
						.append(',')
						.append("Current Status")
						.append(",")
						.append("Status History")
						.append('\n')

						.append(key)
						.append(",")
						.append(startDate)
						.append(",")
						.append(endDate)
						.append(",")
						.append(storyPoint)
						.append(",")
						.append(assignee)
						.append(",")
						.append("working on it")
						.append(",")
						.append(currentStatus)
						.append(",")
						.append((transtitionHistory.getFromString()+","+transtitionHistory.getToString()).toString())
						.toString();
				System.out.println(line);
				csvWriter.write(line);
				csvWriter.newLine();
				csvWriter.flush(); 
				csvWriter.close();
			} catch (IOException e) {
			}

			XSSFWorkbook wb = new XSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			Sheet sheet = wb.createSheet("new sheet");



			System.out.println(issue.size());

			// Create a row and put some cells in it. Rows are 0 based.
			Row row = sheet.createRow(i);
			// Create a cell and put a value in it.
			Cell cell = row.createCell(0);
			cell.setCellValue(key);

			// Or do it on one line.
			row.createCell(1).setCellValue(startDate);
			row.createCell(2).setCellValue(endDate);
			row.createCell(3).setCellValue(currentStatus);
			row.createCell(4).setCellValue(storyPoint);
			row.createCell(5).setCellValue(assignee);
			for(int l = 0; l< listOfFromString.size(); l++) {
				row.createCell(6).setCellValue(transtitionHistory.getFromString()+" -> " + transtitionHistory.getToString());
				listOfFromString.clear();
				listOfToString.clear();
			}
			try (OutputStream fileOut = new FileOutputStream(path + "workbook.xls")) {
				wb.write(fileOut);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
}




