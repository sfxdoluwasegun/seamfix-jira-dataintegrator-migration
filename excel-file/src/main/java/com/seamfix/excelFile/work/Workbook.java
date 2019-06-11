package com.seamfix.excelFile.work;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.seamfix.excelFile.models.QueryData;

@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;

	public  String getIssue() {
		String target ="http://localhost:8080/issue/" +dataBean.getSprintID();
		System.out.println(target);
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
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

	public void getFile() {
		JsonArray root = Json.createReader(new StringReader(getIssue())).readArray();
		BufferedWriter csvWriter = null;
		String path = "C:\\Users\\Seamfix\\Desktop\\Atlassian-JIRA\\Atlassian-Issue\\csvfile.csv";


		List<JsonArray> listOfFromString = new ArrayList<>();
		List<JsonArray> listOfToString = new ArrayList<>();

		List<JsonObject> allIssues = root
				.stream()
				.filter(issue -> issue.asJsonObject().containsKey("Task ID"))
				.map(issue -> issue.asJsonObject())
				.collect(Collectors.toList());
		System.out.println(allIssues);


		for(int i =0; i < allIssues.size(); i++) {
			JsonObject issue = allIssues.get(i);
			String key = issue.getString("Task ID");
			System.out.println(key);
			String startDate = issue.getString("Start Date");
			System.out.println(startDate);
			String endDate = issue.getString("End Date");
			System.out.println(endDate);
			String currentStatus = issue.getString("Current Status");
			System.out.println(currentStatus);
			String storyPoint = issue.getString("Story Point");
			System.out.println(storyPoint);
			String assignee = issue.getString("Reporter");
			System.out.println(assignee);

			JsonArray fromString = issue.asJsonObject().getJsonArray("fromString");
			listOfFromString.add(i, fromString);
			System.out.println(fromString);
			JsonArray toString =  issue.asJsonObject().getJsonArray("toString");
			listOfToString.add(i, toString);
			System.out.println(toString);


			String filePath = "C:\\jcodes\\RND\\jira-dataintegrator";

			XSSFWorkbook wb = new XSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			Sheet sheet = wb.createSheet("new sheet");

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
			row.createCell(6).setCellValue((listOfFromString.toString().replaceAll(",", " -> ")+"\n" + listOfToString.toString().replaceAll(",", " -> ")));
			try (OutputStream fileOut = new FileOutputStream(filePath +"workbook.xlsx")) {
				wb.write(fileOut);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				File csvFile = new File(path);
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
						.append( storyPoint)
						.append(",")
						.append(assignee)
						.append(",")
						.append("working")
						.append(",")
						.append(currentStatus)
						.append(",")
						.append(listOfFromString.toString().replaceAll(",", " -> ")+"\n" +  ","  + "," + "," + "," + "," + "," + ","+ listOfToString.toString().replaceAll(",", " -> "))
						.toString();

				System.out.println(line);
				csvWriter.write(line);
				csvWriter.newLine();
				csvWriter.flush();
				csvWriter.close();
			} catch (IOException e) {
			}
		}
	}

}
