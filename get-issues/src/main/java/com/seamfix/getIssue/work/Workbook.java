package com.seamfix.getIssue.work;

import java.io.StringReader;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.seamfix.getIssue.model.QueryData;
import com.seamfix.getIssue.props.PropertiesManager;


@Dependent
public class Workbook {

	@Inject
	QueryData dataBean;
	
	@Inject
	PropertiesManager propertiesManager;

	@Inject
	Logger logger;

	public  String getIssue(String key) {
		String target =propertiesManager.getProperty("getIssueUrl","https://seamfix.atlassian.net/rest/api/3/issue/" + key);
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", dataBean.getAuth())
					.get(String.class);
		} finally {
			if (client != null) {
				client.close();
			}

		}
	}
	
	public  void getWorklog(){
		String key = dataBean.getTaskID();
		
		if (getIssue(key) == null) {
			prepareErrorMessage(Status.NOT_FOUND, "Connection Error", "Couldn't connect the the JIRA API");
			return;
		}
		
		JsonObject root = Json.createReader(new StringReader(getIssue(key))).readObject();

		if (root == null) {
			prepareErrorMessage(Status.FORBIDDEN, "Worklog Error", "Can't get worklog");
			return;
		}
		
		String taskID = root.getString("key");
		dataBean.setTaskID(taskID);

		JsonObject values = root.getJsonObject("fields");
		if (values == null || values.isEmpty()) {
			prepareErrorMessage(Status.FORBIDDEN, "Worklog Error", "Can't get worklog");
			return;
		}

		boolean workLogJson = values.asJsonObject().getJsonObject("worklog")
				.getJsonArray("worklogs").isEmpty();
		if(workLogJson) {
			dataBean.setWorklog("No Worklog");
		} 
		else if (!values.asJsonObject().getJsonObject("worklog")
				.getJsonArray("worklogs").getJsonObject(0).containsKey("comment")) {
			dataBean.setWorklog("No Worklog");
		}
		else if(values.asJsonObject().getJsonObject("worklog")
				.getJsonArray("worklogs").getJsonObject(0).getJsonObject("comment").isEmpty()) {
			dataBean.setWorklog("No Worklog");
		}else {
			String worklog = values.asJsonObject().getJsonObject("worklog")
					.getJsonArray("worklogs").getJsonObject(0).getJsonObject("comment")
					.getJsonArray("content").getJsonObject(0).getJsonArray("content").getJsonObject(0).getString("text");
			dataBean.setWorklog(worklog);
		}
	}
	
	private void prepareErrorMessage(Status status, String error, String message) {
		dataBean.setStatus(status);
		dataBean.setError(error);
		dataBean.setMessage(message);
	}
}
