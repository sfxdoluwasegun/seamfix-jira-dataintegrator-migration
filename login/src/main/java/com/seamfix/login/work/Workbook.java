package com.seamfix.login.work;

import java.io.StringWriter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import com.seamfix.login.models.QueryData;

@Dependent
public class Workbook {
	@Inject
	QueryData dataBean;
	
	public String createJson() {

		JsonObject json = Json.createObjectBuilder()
				.add("email",dataBean.getEmail())
				.add("password",dataBean.getPassword())
				.build();
		StringWriter sWriter = new StringWriter();
		try (JsonWriter writer = Json.createWriter(sWriter)) {
			writer.write(json);
		}
		return sWriter.toString();
	}
	
}