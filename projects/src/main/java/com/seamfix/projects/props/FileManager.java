package com.seamfix.projects.props;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class FileManager {
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	private String targetPropertyFilepath ;

	private Properties properties;

	/**
	 * Load properties.
	 *
	 * @return the properties
	 */
	public Properties loadProperties() {
		
		properties = new Properties();
		
		try (FileReader fis = new FileReader(getTargetPropertyFilepath())) {
			properties.load(fis);
		} catch (FileNotFoundException e) {
			createProperties();
			log.log(Level.SEVERE, "PropertiesFileNotFound:", e);
		} catch (IOException e) {
			log.log(Level.SEVERE, "PropertiesFileIOError:", e);
		}
		return properties;
	}

	/**
	 * Creates a blank properties file.
	 */
	private void createProperties() {
		
		try (FileOutputStream fos = new FileOutputStream(getTargetPropertyFilepath())) {
			properties.store(fos, "Auto generated document");
			fos.flush();
		} catch (Exception e) {
			log.log(Level.WARNING, "Exception while updating properties file: ", e);
		}
	}

	/**
	 *
	 * @param propertyMap - map to add to property file
	 */
	public void saveProperties(HashMap<String, String> propertyMap) {

		Set<String> keys = propertyMap.keySet();
		Iterator<String> itr = keys.iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			properties.setProperty(key, propertyMap.get(key));
		}
		createProperties();
	}

	/**
	 * deletes an entry with the specified key.
	 *
	 * @param key reference item to remove
	 */
	public synchronized void removeProperty(String key) {
		properties.remove(key);
		createProperties();
	}

	/**
	 * Get the string property but also writes property to file if property doesn't
	 * exist in file.
	 * 
	 * @param key - key of the property
	 * @param defaultVal - property default value if key doesn't exist
	 * @return property
	 */
	public String getProperty(String property, 
			String defaultVal){

		HashMap<String, String> propertyMap = null;

		String props = properties.getProperty(property, "");
		if (props == null || props.isEmpty()){
			propertyMap = new HashMap<String, String>();
			propertyMap.put(property, defaultVal);
		}else
			return props;

		if (propertyMap != null)
			saveProperties(propertyMap);

		return defaultVal;
	}


	/**
	 * Gets property from file and casts to integer. Writes property to file if it
	 * doesn't already exist.
	 *
	 * @param key        reference key
	 * @param defaultVal value if property is not set
	 * @return integer property
	 */
	public Integer getInt(String key, Integer defaultVal) {
		return Integer.valueOf(getProperty(key, defaultVal + ""));
	}

	/**
	 * Gets property from file and casts to long. Writes property to file if it
	 * doesn't already exist.
	 *
	 * @param key        reference key
	 * @param defaultVal value if property is not set
	 * @return integer property
	 */
	public Long getLong(String key, Long defaultVal) {
		return Long.parseLong(getProperty(key, defaultVal + ""));
	}

	/**
	 * Gets the boolean property for specified key. Writes property to file if it
	 * doesn't already exist.
	 *
	 * @param key        the property key
	 * @param defaultVal the default value if nothing is found
	 * @return the bool value for the target key
	 */
	public Boolean getBool(String key, Boolean defaultVal) {
		return Boolean.parseBoolean(getProperty(key, defaultVal.toString()));
	}

}
