package net.buddat.wplanner.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author budda
 * 
 * Updater class will check given file against remote file
 * and if they are not the same, will download the fine and
 * replace local file.
 */
public class Updater {

	private final static Logger LOGGER = Logger.getLogger(Updater.class
			.getName());

	/**
	 * Checks the given file against remote file, downloads the
	 * remote file to local location if the files do not match
	 * 
	 * @param fileName the full name of the file to update
	 * @param host the remote location to get the file from
	 * @param destination the local location of the fine
	 * @return true if files successfully match
	 */
	public static boolean update(String fileName, String host,
			String destination) {
		if (!destination.endsWith(File.separator))
			destination += File.separator;
		if (!host.endsWith("/"))
			host += "/";

		File dest = new File(destination);
		if (!dest.exists())
			dest.mkdirs();

		File toUpdate = new File(destination + fileName);

		URL source;
		URLConnection uc = null;
		try {
			source = new URL(host + fileName);
			uc = source.openConnection();

			if (uc.getLastModified() == toUpdate.lastModified())
				if (uc.getContentLength() == getFileLength(toUpdate)) {
					LOGGER.log(Level.INFO, "File has not changed: "
							+ fileName);
					return true;
				}
		} catch (MalformedURLException e) {
			LOGGER.log(Level.WARNING, "Malformed URL: " + host);
			return false;
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Unable to connect to host: " + host);
			return false;
		}

		return downloadFile(source, toUpdate, uc);
	}

	/**
	 * Gets the length of the given file
	 * 
	 * @param toUpdate file to get length of
	 * @return length of file
	 */
	private static long getFileLength(File toUpdate) {
		try {
			RandomAccessFile raf = new RandomAccessFile(toUpdate, "r");
			long length = raf.length();
			raf.close();

			return length;
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.WARNING,
					"File doesn't exist: " + toUpdate.getName());
		} catch (IOException e) {
			LOGGER.log(Level.WARNING,
					"Unable to read file: " + toUpdate.getName());
		}

		return 0;
	}

	/**
	 * Downloads the given file to specified local location
	 * 
	 * @param source remote location of file to download
	 * @param dest local destination to download file to
	 * @param uc connection to use to download file
	 * @return true if file is downloaded and saved successfully
	 */
	private static boolean downloadFile(URL source, File dest, URLConnection uc) {
		LOGGER.log(Level.INFO, "Downloading file: " + source.toString());

		try {
			ReadableByteChannel rbc = Channels.newChannel(uc.getInputStream());
			FileOutputStream fos = new FileOutputStream(dest);
			fos.getChannel().transferFrom(rbc, 0, 1 << 24);
			fos.close();

			LOGGER.log(Level.INFO, "File downloaded: " + dest.getAbsolutePath());
			return dest.setLastModified(uc.getLastModified());
		} catch (MalformedURLException e) {
			LOGGER.log(Level.WARNING,
					"Unable to connect to host: " + source.toString());
			return false;
		} catch (IOException e) {
			LOGGER.log(Level.WARNING,
					"Unable to download file: " + dest.getName());
			return false;
		}
	}
}
