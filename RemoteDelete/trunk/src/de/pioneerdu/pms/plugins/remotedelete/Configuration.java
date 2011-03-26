package de.pioneerdu.pms.plugins.remotedelete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	protected static Properties rdConfiguration = new Properties();
	protected static Boolean rdConfigUpdated = false;
	protected static File propFile = new File("RemoteDelete.conf");
	
	public Configuration() {	
		if (propFile.exists()) {
			FileInputStream is;
			try {
				is = new FileInputStream(propFile);
				rdConfiguration.load(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
		setIfMissing("rdDeleteFolder", "PMS_Trashcan");
		setIfMissing("rdLogEnabled", "true");
		setIfMissing("rdLogPath", "RemoteDelete.log");
		setIfMissing("rdAsyncMoveQueueInterval", "60");
		setIfMissing("rdKeepTrashcanOnDisc", "true");
		setIfMissing("rdKeepAsyncMoveQueueOnDisc", "true");
		setIfMissing("rdKeepFolderStructureInTrashcan", "true");
		saveIfUpdated();
	}

	public String getDeleteFolder() {
		return rdConfiguration.getProperty("rdDeleteFolder");
	}
	
	public Boolean getLogEnabled() {
		return (rdConfiguration.getProperty("rdDeleteFolder") == "true") ? true : false;
	}
	
	public String getLogPath() {
		return rdConfiguration.getProperty("rdLogPath");
	}
	
	public int getAsyncMoveQueueInterval() {
		return Integer.parseInt(rdConfiguration.getProperty("rdAsyncMoveQueueInterval"));
	}
	
	public Boolean getKeepTrashcanOnDisc() {
		return (rdConfiguration.getProperty("rdKeepTrashcanOnDisc") == "true") ? true : false;
	}

	public Boolean getKeepAsyncMoveQueueOnDisc() {
		return (rdConfiguration.getProperty("rdKeepAsyncMoveQueueOnDisc") == "true") ? true : false;
	}

	public Boolean getKeepFolderStructureInTrashcan() {
		return (rdConfiguration.getProperty("rdKeepFolderStructureInTrashcan") == "true") ? true : false;
	}

	private void saveIfUpdated() {
		if (rdConfigUpdated) {
			FileOutputStream os;
			try {
				os = new FileOutputStream(propFile);
				rdConfiguration.store(os, null);
				rdConfigUpdated = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void save() {
		rdConfigUpdated = true;
		saveIfUpdated();
	}
	
	private void setIfMissing(String key, String value) {
		if (!rdConfiguration.containsKey(key)) {
			rdConfiguration.setProperty(key, value);
			rdConfigUpdated = true;
		}
	}

	public Properties getConfiguration() {
		return rdConfiguration;
	}
}
