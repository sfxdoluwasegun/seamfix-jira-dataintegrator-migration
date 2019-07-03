package com.seamfix.login.work;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Base64;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.seamfix.login.models.QueryData;

@Dependent
public class Workbook {
	@Inject
	QueryData dataBean;

	public String createJson() {

		JsonObject json = Json.createObjectBuilder()
				.add("email",dataBean.getEmail())
				.add("token",dataBean.getToken())
				.build();
		StringWriter sWriter = new StringWriter();
		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json);
		}
		return sWriter.toString();
	}

	public  String getAuthHeader() {
		String email = dataBean.getEmail().trim();
		String token= dataBean.getToken().trim();
		String auth = email +":"+ token;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(Charset.forName("ISO-8859-1")));
		String finalAuth = "Basic " + encodedAuth;
		return finalAuth;
	}

	public  Response check() {
		String target ="https://seamfix.atlassian.net/rest/agile/1.0/board/";
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", getAuthHeader())
					.get(Response.class);
		} finally {
			if (client != null)
				client.close();
		}
	}
	
	public void getRespone() {
		int code = check().getStatus();
		if(code == 401 ) {
			prepareErrorMessage(Status.UNAUTHORIZED, "Login Error", "email or token is wrong");
			return;
		} else {
			dataBean.setAuth(getAuthHeader());
		}
	}
	
	private void prepareErrorMessage(Status status, String error, String message) {
		dataBean.setStatus(status);
		dataBean.setError(error);
		dataBean.setMessage(message);
	}
}