package com.seamfix.kanban.props;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Caches property file for use in application life cycle.
 * 
 * @author segz
 *
 */
public class FileCache {
	
	private static ConcurrentHashMap<String, FileManager> pties = new ConcurrentHashMap<String, FileManager>();

	/**
	 * Retrieves the property object by name
	 *
	 * @param file the file
	 * @return the property file
	 */
	public static FileManager getPropertyFile(String filepath) {
		String filename = new File(filepath).getName();
		FileManager pf = pties.get(filename);
		if(pf == null){
			pf = new FileManager(filepath);
			pf = pties.putIfAbsent(filename, pf);
		}
		return pf;
	}

	/**
	 * Retrieves the default property object
	 *
	 * @return the property file
	 */
	public static FileManager getPropertyFile() {
		String filepath = "C:/config.properties";
		FileManager pf = pties.get(filepath);
		if(pf == null){
			pf = new FileManager(filepath);
			pf = pties.putIfAbsent(filepath, pf);
		}
		return pf;
	}

}
