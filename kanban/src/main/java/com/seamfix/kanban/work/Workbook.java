package com.seamfix.kanban.work;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.seamfix.kanban.models.ExcelFile;
import com.seamfix.kanban.models.Issues;
import com.seamfix.kanban.models.Parent;
import com.seamfix.kanban.models.QueryData;

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
	
	 // Method to encode a string value using `UTF-8` encoding scheme
    private static String encodetarget(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

	public  String kanbanIssue() {
		String url ="https://seamfix.atlassian.net/rest/api/2/search?jql=";
		String target =url+encodetarget("project = " + dataBean.getProjectName()+ " and created >="+dataBean.getStartDate()+" and created <= "+dataBean.getEndDate())+"&startAt="+dataBean.getStartAt()+"&maxResults="+dataBean.getMaxResults();
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

	public String recieveResponse(String target,String key, String jsonRequest) throws BadRequestException, ServiceUnavailableException, WebApplicationException {
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
		String target = "http://localhost:8088/changelog/"+key;
		System.out.println(target);
		String response = recieveResponse(target,key, json);
		return Json.createReader(new StringReader(response)).readObject();
	}

	private JsonObject postLog(String key, String json) {
		String target ="http://localhost:8087/getIssue/"+ key;
		System.out.println(target);
		String response = recieveResponse(target,key, json);
		return Json.createReader(new StringReader(response)).readObject();
	}

	public List<JsonObject> callLog() {

		JsonObject root = Json.createReader(new StringReader(kanbanIssue())).readObject();

		JsonArray issues = root.getJsonArray("issues");


		List<JsonObject> filteredValues = issues
				.stream()
				.filter(issue -> issue.asJsonObject().getString("expand").equals("operations,versionedRepresentations,editmeta,changelog,customfield_11919.properties,renderedFields"))
				.map(issue -> issue.asJsonObject())
				.collect(Collectors.toList());
		System.out.println(filteredValues.size());
		return filteredValues;
	}

	public void getParentKeys() {
		List<JsonObject> filteredValues = callLog();

		List<String> listOfAuthors = new ArrayList<>();
		List<Double> listOfPoints = new ArrayList<>();
		List<Double> listOfIncomplete = new ArrayList<>();

		for(int i =0; i < filteredValues.size(); i++) {
			Issues	issuesq  = new Issues();
			JsonObject issue = filteredValues.get(i);

			String id = issue.getString("id");
			System.out.println(id);
			issuesq.setId(id);

			String key = issue.getString("key");
			System.out.println(key);
			issuesq.setKey(key);

			String assignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
			System.out.println(assignee);
			issuesq.setAssignee(assignee);
			listOfAuthors.add(assignee);

			dataBean.getIssues().add(issuesq);

			JsonArray subtasks = issue.getJsonObject("fields").getJsonArray("subtasks");
			if(!subtasks.isEmpty()) {

				Parent parent = new Parent();

				String sid = issue.getString("id");
				System.out.println(sid);
				parent.setId(sid);

				String skey = issue.getString("key");
				System.out.println(skey);
				parent.setKey(skey);

				String PAssignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
				System.out.println();
				parent.setAssignee(PAssignee);

				String jsonString = createJson(skey);

				JsonObject jsonObject = postService(skey,jsonString);

				if(jsonObject == null) {
					return;
				}

				String currentStatus = jsonObject.getJsonObject("issues").getString("currentStatus");
				parent.setCurrentStatus(currentStatus);

				String storyPoint = jsonObject.getJsonObject("issues").getString("storyPoint");
				parent.setStoryPoint(storyPoint);

				Double sum = Double.parseDouble(storyPoint);
				System.out.println(sum);
				listOfPoints.add(sum);

				dataBean.getParent().add(parent);

				if(!issue.getJsonObject("fields").containsKey("closedSprints")) {
					System.out.println("no closed sprint");

				}else {
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
		System.out.println(totalMemebers);
		dataBean.setMembers(totalMemebers);

		Double totalPoints = listOfPoints.stream().mapToDouble(Double::doubleValue).sum();
		System.out.println(totalPoints);
		dataBean.setTotalPoints(totalPoints);

		Double points = listOfIncomplete.stream().mapToDouble(Double::doubleValue).sum();
		Double incompletePoints = totalPoints - points;
		System.out.println(incompletePoints );
		dataBean.setCompletePoints(incompletePoints);

	}

	public void getAllIssues() {
		
		List<JsonObject> filteredValues = callLog();

		for(int i =0; i < filteredValues.size(); i++) {
			ExcelFile file = new ExcelFile();
			List<String> listOfFromString = new ArrayList<>();
			JsonObject issue = filteredValues.get(i);

			String key = issue.getString("key");
			System.out.println(key);
			file.setKey(key);

			String assignee = issue.getJsonObject("fields").getJsonObject("assignee").getString("displayName");
			System.out.println(assignee);
			file.setAssignee(assignee);

			String jsonString = createJson(key);

			JsonObject jsonObject = postService(key,jsonString);

			JsonObject logObject = postLog(key, jsonString);

			if(logObject == null) {

			}

			String worklog = logObject.getString("Worklog");
			System.out.println(worklog);
			file.setWorklog(worklog);

			if(jsonObject == null) {
				return;
			}
			JsonObject json = jsonObject.getJsonObject("issues");

			String startDate = json.getString("startDate");
			System.out.println(startDate);
			file.setDateCreated(startDate);

			String endDate = json.getString("endDate");
			file.setDateModified(endDate);

			String currentStatus = json.getString("currentStatus");
			System.out.println(currentStatus);
			file.setCurrentStatus(currentStatus);

			String storyPoint = json.getString("storyPoint");
			file.setStoryPoint(storyPoint);


			JsonArray hFromString = json.getJsonObject("flow").getJsonArray("fromString");
			for(int j =0; j< hFromString.size(); j++) {
				String fromString = hFromString.getString(j);
				listOfFromString.add(fromString);
				System.out.println(listOfFromString);
			}
			file.setFromString(listOfFromString);

			listOfFromString.add(currentStatus);
			if(!listOfFromString.contains("In QA Review")) {
				file.setCount("No QA Review");
			} else {
				int number = Collections.frequency(listOfFromString, "In QA Review");
				System.out.println(Collections.frequency(listOfFromString, "In QA Review"));
				String count = String.valueOf(number);
				System.out.println(count);
				file.setCount(count);
			}

			dataBean.getFile().add(file);

			//create blank workbook
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
				System.out.println(excelFile.getKey());

				row.createCell(1)
				.setCellValue(excelFile.getAssignee());
				System.out.println(excelFile.getAssignee());

				row.createCell(2)
				.setCellValue(excelFile.getDateCreated());
				System.out.println(excelFile.getDateCreated());

				row.createCell(3)
				.setCellValue(excelFile.getDateModified());
				System.out.println(excelFile.getDateModified());

				row.createCell(4)
				.setCellValue(excelFile.getCurrentStatus());
				System.out.println(excelFile.getCurrentStatus());

				row.createCell(5)
				.setCellValue(excelFile.getStoryPoint());
				System.out.println(excelFile.getStoryPoint());


				row.createCell(6)
				.setCellValue(excelFile.getWorklog());
				System.out.println(excelFile.getWorklog());

				row.createCell(7)
				.setCellValue(excelFile.getFromString().toString().replaceAll(",", " -> "));
				System.out.println(excelFile.getFromString());

				row.createCell(8)
				.setCellValue(excelFile.getCount());
				System.out.println(excelFile.getCount());

			}
			// Resize all columns to fit the content size
			for(int p = 0; p < columns.length; p++) {
				sheet.autoSizeColumn(p);
			}
			// Write the output to a file
			String path = "C:\\jcodes\\RND\\jira-dataintegrator\\";
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(path + dataBean.getProjectID()+"-"+"Log.xlsx");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				workbook.write(fileOut);
				fileOut.close();
				// Closing the workbook
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
