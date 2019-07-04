package com.seamfix.kanban.props;

import javax.enterprise.context.ApplicationScoped;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class PropertiesManager extends FileManager {

	private String targetPropertyFilepath ;

	public PropertiesManager() {
		super.propsManager = FileCache.getPropertyFile();
	}

	public PropertiesManager(String filepath) {
		
		setTargetPropertyFilepath(filepath);
		propsManager = FileCache.getPropertyFile(getTargetPropertyFilepath());
	}
}
