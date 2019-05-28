package com.seamfix.sprint.work;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;

import com.seamfix.sprint.model.QueryData;

@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;

	private String getAuthHeader() {
		String email = "mabikoye@seamfix.com";
		String token= "wXtzMKuBuOmzoRJJrNDtCF23";
		String auth = email +":"+ token;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(Charset.forName("ISO-8859-1")));
		return "Basic " + encodedAuth;
	}


	public  String getSprint() {
		String target = "http://seamfix.atlassian.net/rest/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId=" + dataBean.getProjectID() + "&sprintId="+ dataBean.getSprintID();
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
	
	public void getJSON() {
		
		JsonObject root = Json.createReader(new StringReader(getSprint())).readObject();
		
        JsonObject contents = root.getJsonObject("contents");
         String totalStoryPoint = contents.asJsonObject().getJsonObject("allIssuesEstimateSum").getString("text");
         System.out.println(totalStoryPoint);
         dataBean.setTotalStoryPoint(totalStoryPoint);
         
         String completeStoryPoint = contents.asJsonObject().getJsonObject("completedIssuesEstimateSum").getString("text");
         System.out.println(completeStoryPoint);
         dataBean.setCompleteStoryPoint(completeStoryPoint);
         
         String startDate = contents.asJsonObject().getJsonObject("sprint").getString("isoStartDate");
         System.out.println(startDate);
         dataBean.setStartDate(startDate);
         
         String endDate = contents.asJsonObject().getJsonObject("sprint").getString("isoEndDate");
         System.out.println(endDate);
         dataBean.setEndDate(endDate);
	}
}
