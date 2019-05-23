package com.seamfix.sprint.work;

import java.nio.charset.Charset;
import java.util.Base64;

import javax.enterprise.context.Dependent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;

@Dependent
public class Workbook {

	private String getAuthHeader() {
		String email = "mabikoye@seamfix.com";
		String token= "wXtzMKuBuOmzoRJJrNDtCF23";
		String auth = email +":"+ token;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(Charset.forName("ISO-8859-1")));
		return "Basic " + encodedAuth;
	}


	public  String getSprint(int projectID, int sprintID ) {
		String target = "http://seamfix.atlassian.net/rest/greenhopper/1.0/rapid/charts/sprintreport?rapidViewId=" + projectID + "&sprintId="+ sprintID;
		System.out.println(target);
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			System.out.println("target");
			return client.target(target.trim())
					.request(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION,getAuthHeader())
					.get(String.class);
		} finally {
			if (client != null)
				client.close();

		}
	}
}
