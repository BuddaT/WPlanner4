package net.buddat.wplanner.settings;


public class AppSettings {

	public static final String PROGRAM_NAME = "WPlanner";
	
	public static final int VERSION_MAJOR = 4, 	// Major version changes
			VERSION_MIDI = 0, // Changes to save file that may break backwards compatibility
			VERSION_MINOR = 0; // Small updates

	public static final String SETTINGS_FILE = PROGRAM_NAME.toLowerCase()
			+ ".cfg";
	/*
	 * Only return the MAJOR & MIDI versions as this is used for file storage,
	 * namely maps.
	 */
	public static final String getVersionString() {
		return VERSION_MAJOR + "." + VERSION_MIDI;
	}

}
