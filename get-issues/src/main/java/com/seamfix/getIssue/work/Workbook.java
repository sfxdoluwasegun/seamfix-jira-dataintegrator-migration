package com.seamfix.getIssue.work;

import java.io.StringReader;
import java.util.Base64;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.seamfix.getIssue.model.QueryData;


@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;

	private String getAuthHeader() {
		String email = "mabikoye@seamfix.com";
		String token= "wXtzMKuBuOmzoRJJrNDtCF23";
		String auth = email +":"+ token;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
		return "Basic " + encodedAuth;
	}

	public  String getIssue(String key) {
		String target = "https://seamfix.atlassian.net/rest/api/3/issue/" + key;
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
		JsonObject root = Json.createReader(new StringReader(getIssue(key))).readObject();

		String taskID = root.getString("key");
		dataBean.setTaskID(taskID);

		JsonObject values = root.getJsonObject("fields");

		boolean workLogJson = values.asJsonObject().getJsonObject("worklog")
				.getJsonArray("worklogs").isEmpty();

		if(workLogJson) {
			dataBean.setWorklog("No Worklog");
		} else {
			String worklog = values.asJsonObject().getJsonObject("worklog")
					.getJsonArray("worklogs").getJsonObject(0).getJsonObject("comment")
					.getJsonArray("content").getJsonObject(0).getJsonArray("content").getJsonObject(0).getString("text");
			dataBean.setWorklog(worklog);
		}
	}
}
