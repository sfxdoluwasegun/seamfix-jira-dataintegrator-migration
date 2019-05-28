package com.seamfix.sprint.work;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpHeaders;

import com.seamfix.sprint.model.QueryData;

@Dependent
public class Workbook {
	
	private final Logger log = Logger.getLogger(Workbook.class.getName());

	@Inject
	QueryData dataBean;

	public  String getSprint() {
		String target = "http://seamfix.atlassian.net/rest/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId=" + dataBean.getProjectID() + "&sprintId="+ dataBean.getSprintID();
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			Response response = client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(dataBean.getAuth().getBytes(Charset.forName("ISO-8859-1"))))
					.get(Response.class);
			if (response == null)
				throw new InternalServerErrorException();

			int status = response.getStatus();
			
			if (status == 403)
				throw new ForbiddenException();
			if (status != 200)
				throw new BadRequestException();
			
			String jsonString = response.readEntity(String.class);
			log.log(Level.INFO, "Response status code: {0} Response body: {1}", new Object[]{status, jsonString});
			
			return jsonString;
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
