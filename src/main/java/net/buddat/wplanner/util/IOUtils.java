package net.buddat.wplanner.util;

import java.io.File;

import net.buddat.wplanner.settings.AppSettings;

public class IOUtils {

	public static final String NEW_LINE = System.getProperty("line.separator");

	public static String getUserDataDirectoryString() {
		return System.getProperty("user.home") + File.separator + "."
				+ AppSettings.PROGRAM_NAME.toLowerCase()
				+ File.separator + AppSettings.getVersionString()
				+ File.separator;
	}

}
