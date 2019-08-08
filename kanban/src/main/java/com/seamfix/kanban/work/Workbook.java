package com.seamfix.kanban.work;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.seamfix.kanban.models.ExcelFile;
import com.seamfix.kanban.models.Issues;
import com.seamfix.kanban.models.Parent;
import com.seamfix.kanban.models.QueryData;
import com.seamfix.kanban.props.PropertiesManager;

@Dependent
public class Workbook {
	@Inject
	QueryData dataBean;

	@Inject
	PropertiesManager propertiesManager;

	@Inject
	Logger logger;

	// Method to encode a string value using `UTF-8` encoding scheme
	private static String encodetarget(String value) {
		try {
			return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}
	}

	public  String kanbanIssue() {
		String url =propertiesManager.getProperty("kanbanUrl", "https://seamfix.atlassian.net/rest/api/2/search?jql=");
		if(dataBean.getMaxResults() == 0) {
			dataBean.setMaxResults(100);
		}
		String target =url+encodetarget("project = " + dataBean.getProjectName()+ " and created >= "+dataBean.getStartDate()+" and created <= "+dataBean.getEndDate())+"&startAt="+dataBean.getStartAt()+"&maxResults="+dataBean.getMaxResults();
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", dataBean.getAuth())
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

	public String recieveResponse(String target,String key, String jsonRequest) throws BadRequestException, ServiceUnavailableException, WebApplicationException {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request()
					.header("Authorization", dataBean.getAuth())
					.post(Entity.entity(jsonRequest, MediaType.APPLICATION_JSON), String.class);
		} finally {
			if (client != null)
				client.close();
		}
	}

	private JsonObject postService(String key, String json) {
		String changelog= propertiesManager.getProperty("changelogPath", "http://localhost:8088/changelog/");
		String target = changelog + key;
		String response = recieveResponse(target,key, json);
		
		if(response == null) {
			prepareErrorMessage(Status.EXPECTATION_FAILED, "Changelog Error", "Error getting the changelog. Please retry");
			return null;
		}
		return Json.createReader(new StringReader(response)).readObject();
	}

	private JsonObject postLog(String key, String json) {
		String getIssue= propertiesManager.getProperty("getIssuePath", "http://localhost:8087/get-issue/");
		String target = getIssue + key;
		String response = recieveResponse(target,key, json);
		if(response == null) {
			prepareErrorMessage(Status.EXPECTATION_FAILED, "Worklog Error", "Error getting the worklog. Please retry");
			return null;
		}
		return Json.createReader(new StringReader(response)).readObject();
	}

	public List<JsonObject> callLog() {
		if (kanbanIssue() == null) {
			prepareErrorMessage(Status.NOT_FOUND, "Connection Error", "Couldn't connect to JIRA API");
			return null;
		}
		JsonObject root = Json.createReader(new StringReader(kanbanIssue())).readObject();

		JsonArray issues = root.getJsonArray("issues");

		List<JsonObject> filteredValues = issues
				.stream()
				.filter(issue -> issue.asJsonObject().getString("expand").equals("operations,versionedRepresentations,editmeta,changelog,renderedFields"))
				.map(issue -> issue.asJsonObject())
				.collect(Collectors.toList());
		return filteredValues;
	}

	public void getParentKeys() {
		if (kanbanIssue() == null) {
			prepareErrorMessage(Status.NOT_FOUND, "Connection Error", "Couldn't connect to JIRA API");
			return;
		}
		JsonObject root = Json.createReader(new StringReader(kanbanIssue())).readObject();
		if ( root == null) {
			prepareErrorMessage(Status.FORBIDDEN, "Issue Error", "No Issue");
			return;
		}

		List<JsonObject> filteredValues = callLog();
		
		if (filteredValues.isEmpty() || filteredValues == null) {
			prepareErrorMessage(Status.FORBIDDEN, "Issue Error", "No Issue");
			return;
		}
		
		List<String> listOfAuthors = new ArrayList<>();
		List<Double> listOfPoints = new ArrayList<>();
		List<Double> listOfIncomplete = new ArrayList<>();


		int total = root.getInt("total");
		dataBean.setTotal(total);

		for(JsonObject issue : filteredValues) {
			Issues	issuesq  = new Issues();

			String id = issue.getString("id");
			issuesq.setId(id);

			String key = issue.getString("key");
			issuesq.setKey(key);

			String assignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
			listOfAuthors.add(assignee);
			issuesq.setAssignee(assignee);
			dataBean.getIssues().add(issuesq);

			JsonArray subtasks = issue.getJsonObject("fields").getJsonArray("subtasks");
			if(!subtasks.isEmpty()) {

				Parent parent = new Parent();

				String sid = issue.getString("id");
				parent.setId(sid);

				String skey = issue.getString("key");
				parent.setKey(skey);

				String PAssignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
				parent.setAssignee(PAssignee);

				String jsonString = createJson(skey);

				JsonObject jsonObject = postService(skey,jsonString);

				if(jsonObject == null) {
					prepareErrorMessage(Status.EXPECTATION_FAILED, "Changelog Error", "Error getting the changelog. Please retry");
					return;
				}

				String currentStatus = jsonObject.getJsonObject("issues").getString("currentStatus");
				parent.setCurrentStatus(currentStatus);

				String storyPoint = jsonObject.getJsonObject("issues").getString("storyPoint");
				parent.setStoryPoint(storyPoint);

				Double sum = Double.parseDouble(storyPoint);
				listOfPoints.add(sum);

				dataBean.getParent().add(parent);

				if(issue.getJsonObject("fields").containsKey("closedSprints")) {

					JsonArray closedSprints = issue.getJsonObject("fields").getJsonArray("closedSprints");
					int sprint = closedSprints.getJsonObject(closedSprints.size() - 1).getInt("id");
					if(dataBean.getSprintID() != sprint) {
						String incomplte = jsonObject.getJsonObject("issues").getString("storyPoint");
						Double inCSum = Double.parseDouble(incomplte);
						listOfIncomplete.add(inCSum);
					}
				}
			}
		}
		long totalMemebers = listOfAuthors.stream().distinct().count();	
		dataBean.setMembers(totalMemebers);

		Double totalPoints = listOfPoints.stream().mapToDouble(Double::doubleValue).sum();
		dataBean.setTotalPoints(totalPoints);

		Double points = listOfIncomplete.stream().mapToDouble(Double::doubleValue).sum();
		Double incompletePoints = totalPoints - points;
		dataBean.setCompletePoints(incompletePoints);

	}

	public void getAllIssues() {
	
		List<JsonObject> filteredValues = callLog();
		
		if (filteredValues == null || filteredValues.isEmpty()) {
			prepareErrorMessage(Status.FORBIDDEN, "Issue Error", "No Issue");
			return;
		}

		for(JsonObject issue: filteredValues) {
			ExcelFile file = new ExcelFile();
			List<String> listOfFromString = new ArrayList<>();

			String key = issue.getString("key");
			file.setKey(key);

			String assignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
			file.setAssignee(assignee);

			String jsonString = createJson(key);

			JsonObject jsonObject = postService(key,jsonString);

			JsonObject logObject = postLog(key, jsonString);

			if(logObject == null || logObject.isEmpty()) {
				prepareErrorMessage(Status.EXPECTATION_FAILED, "Worklog Error", "Error getting the worklog. Please retry");
				return;
			}

			String worklog = logObject.getString("Worklog");
			file.setWorklog(worklog);

			if(jsonObject == null || jsonObject.isEmpty()) {
				prepareErrorMessage(Status.EXPECTATION_FAILED, "Changelog Error", "Error getting the changelog. Please retry");
				return;
			}
			JsonObject json = jsonObject.getJsonObject("issues");

			String startDate = json.getString("startDate");
			file.setDateCreated(startDate);

			String endDate = json.getString("endDate");
			file.setDateModified(endDate);

			String currentStatus = json.getString("currentStatus");
			file.setCurrentStatus(currentStatus);

			String storyPoint = json.getString("storyPoint");
			file.setStoryPoint(storyPoint);


			JsonArray hFromString = json.getJsonObject("flow").getJsonArray("fromString");
			for(int j =0; j< hFromString.size(); j++) {
				String fromString = hFromString.getString(j);
				listOfFromString.add(fromString);
			}
			file.setFromString(listOfFromString);

			listOfFromString.add(currentStatus);
			if(!listOfFromString.contains("In QA Review")) {
				file.setCount("No QA Review");
			} else {
				int number = Collections.frequency(listOfFromString, "In QA Review");
				String count = String.valueOf(number);
				file.setCount(count);
			}

			dataBean.getFile().add(file);

			XSSFWorkbook workbook = new XSSFWorkbook(); 

			//Create a blank sheet
			XSSFSheet sheet = workbook.createSheet("Sprint");

			// Create a Row
			Row headerRow = sheet.createRow(0);

			// Create cells
			String[] columns = {"Key","Assignee","Start Date","End Date","Current Status","storyPoint","Worklog","Transition History","QA Review"};

			for(int n = 0; n < columns.length; n++) {
				Cell cell = headerRow.createCell(n);
				cell.setCellValue(columns[n]);

				//to enable newlines you need set a cell styles with wrap=true
				CellStyle cs = workbook.createCellStyle();
				cs.setWrapText(true);
				cell.setCellStyle(cs);
			}

			// Create Other rows and cells with data
			int rowNum = 1;
			for(ExcelFile excelFile: dataBean.getFile()) {
				Row row = sheet.createRow(rowNum++);

				row.createCell(0)
				.setCellValue(excelFile.getKey());

				row.createCell(1)
				.setCellValue(excelFile.getAssignee());
				
				row.createCell(2)
				.setCellValue(excelFile.getDateCreated());
				
				row.createCell(3)
				.setCellValue(excelFile.getDateModified());
				
				row.createCell(4)
				.setCellValue(excelFile.getCurrentStatus());
				
				row.createCell(5)
				.setCellValue(excelFile.getStoryPoint());
				
				row.createCell(6)
				.setCellValue(excelFile.getWorklog());
				
				row.createCell(7)
				.setCellValue(excelFile.getFromString().toString().replaceAll(",", " -> "));
				
				row.createCell(8)
				.setCellValue(excelFile.getCount());
			}
			// Resize all columns to fit the content size
			for(int p = 0; p < columns.length; p++) {
				sheet.autoSizeColumn(p);
			}
			// Write the output to a file
			String sourcePath= propertiesManager.getProperty("kanbanExcelFile","C:\\jcodes\\RND\\jira-dataintegrator\\Downloads\\");
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(sourcePath + dataBean.getProjectName() + "-"+ dataBean.getEndDate()+"-"+"Log.xlsx");
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING, "File Not Found");
			}
			try {
				workbook.write(fileOut);
				fileOut.close();
				// Closing the workbook
				workbook.close();
			} catch (IOException e) {
				logger.log(Level.WARNING,"IOException");
			}
		}
	}

	private void prepareErrorMessage(Status status, String error, String message) {
		dataBean.setStatus(status);
		dataBean.setError(error);
		dataBean.setMessage(message);
	}

}