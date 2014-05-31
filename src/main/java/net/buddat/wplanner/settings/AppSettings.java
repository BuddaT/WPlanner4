package net.buddat.wplanner.settings;

import java.util.ArrayList;

import net.buddat.wplanner.util.IOUtils;

public class AppSettings {

	public static final String PROGRAM_NAME = "WPlanner";
	
	public static final int VERSION_MAJOR = 4, 	// Major version changes
			VERSION_MIDI = 0, // Changes to save file that may break backwards compatibility
			VERSION_MINOR = 0; // Small updates

	public static final String SETTINGS_FILE = IOUtils.getUserDataDirectoryString() + PROGRAM_NAME.toLowerCase()
			+ ".cfg";

	public static final String RESOURCE_URL = "http://buddat.net/wplanner/lib/";
	public static final ArrayList<String> RESOURCE_LIST = new ArrayList<String>();

	public static final String LOG_FILE = IOUtils.getUserDataDirectoryString() + PROGRAM_NAME.toLowerCase() + ".log";

	/*
	 * Only return the MAJOR & MIDI versions as this is used for file storage,
	 * namely maps.
	 */
	public static final String getVersionString() {
		return VERSION_MAJOR + "." + VERSION_MIDI;
	}

	static {
		RESOURCE_LIST.add("res_tiles.zip");
		// RESOURCE_LIST.add("res_houses.zip");
		// RESOURCE_LIST.add("res_fences.zip");
		// RESOURCE_LIST.add("res_objects.zip");
		// RESOURCE_LIST.add("res_misc.zip");
	}
}
