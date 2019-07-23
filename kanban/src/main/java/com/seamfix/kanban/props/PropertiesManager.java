package com.seamfix.kanban.props;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Default
public class PropertiesManager extends FileManager {
	
	@Inject Logger Log ;
	
	@Inject
	@ConfigurationValue(value = "app-properties.file.path")
	private String path ;

	@PostConstruct
	public void init() {
		String filepath = path + "config.properties";
		Log.log(Level.INFO, "PROPERTIES FILE IS LOCATED AT: {0}", filepath);
		setTargetPropertyFilepath(filepath);
		loadProperties();
		FileCache.getPropertyFile(filepath);
	} 
}
