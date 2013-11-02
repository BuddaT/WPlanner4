package net.buddat.wplanner.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.buddat.wplanner.util.IOUtils;

public class Settings {

	// Force a remake of the config file each time the app is launched
	public static final boolean DEV_REMAKE_CONFIG = true;

	private static HashMap<String, String> defaultSettingsMap = new HashMap<String, String>();
	private static HashMap<String, String> settingsMap = new HashMap<String, String>();

	private final static Logger LOGGER = Logger.getLogger(Settings.class
			.getName());

	public static void loadSettings() {
		File settingsFile = new File(IOUtils.getUserDataDirectoryString()
				+ AppSettings.SETTINGS_FILE);

		if (!settingsFile.exists() || DEV_REMAKE_CONFIG) {
			createDefaultSettings(settingsFile);
			settingsMap.putAll(defaultSettingsMap);

			return;
		}

		try {
			loadSettings(settingsFile);
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to read settings file: "
					+ settingsFile.getAbsolutePath());
			settingsMap.putAll(defaultSettingsMap);
		}
	}

	public static boolean saveSettings() {
		File settingsFile = new File(IOUtils.getUserDataDirectoryString()
				+ AppSettings.SETTINGS_FILE);

		try {
			writeSettings(settingsFile, settingsMap);
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, "Unable to write settings file: "
					+ settingsFile.getAbsolutePath());
			return false;
		}

		return true;
	}

	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(getSetting(key));
	}

	public static int getInt(String key) {
		return Integer.parseInt(getSetting(key));
	}

	public static String getSetting(String key) {
		if (settingsMap.containsKey(key))
			return settingsMap.get(key);

		if (defaultSettingsMap.containsKey(key))
			return defaultSettingsMap.get(key);

		return null;
	}

	public static void setSetting(String key, String value, boolean save) {
		settingsMap.put(key, value);

		if (save)
			saveSettings();
	}

	private static void createDefaultSettings(File f) {
		try {
			f.getParentFile().mkdirs();
			f.createNewFile();
			writeSettings(f, defaultSettingsMap);
		} catch (IOException ioe) {
			LOGGER.log(Level.WARNING, "Unable to write default settings file: "
					+ f.getAbsolutePath());
			ioe.printStackTrace();
		}
	}

	private static void loadSettings(File f) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(f));
		
		String line;
		while ((line = br.readLine()) != null) {
			if (line.contains("="))
				settingsMap.put(line.split("=")[0], line.split("=")[1]);
		}

		br.close();
	}

	private static void writeSettings(File f, Map<String, String> toWrite)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));

		for (String key : toWrite.keySet())
			bw.write(key + "=" + toWrite.get(key) + IOUtils.NEW_LINE);

		bw.close();
	}

	public static final String CFG_SINGLE_INSTANCE_KEY = "useSingleInstance",
			CFG_AUTOUPDATE_KEY = "autoUpdate";
	public static final String CFG_SINGLE_INSTANCE_VALUE = "true",
			CFG_AUTOUPDATE_VALUE = "true";

	static {
		defaultSettingsMap.put(CFG_SINGLE_INSTANCE_KEY, CFG_SINGLE_INSTANCE_VALUE);
		defaultSettingsMap.put(CFG_AUTOUPDATE_KEY, CFG_AUTOUPDATE_VALUE);
	}
}
