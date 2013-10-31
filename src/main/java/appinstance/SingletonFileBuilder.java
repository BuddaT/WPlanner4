package appinstance;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File that is to be created exactly once for all running instances of this application. File is created in the
 * temporary directory, with a hash of the application name and with the extension provided.
 */
public class SingletonFileBuilder {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final Logger LOGGER = Logger.getLogger(SingletonFileBuilder.class.getName());

    private final String tmpDir;

    public SingletonFileBuilder() {
        String newTmpDir = System.getProperty("java.io.tmpdir");
        if (!newTmpDir.endsWith(FILE_SEPARATOR)) {
            newTmpDir += FILE_SEPARATOR;
        }
        tmpDir = newTmpDir;
    }

    public File buildSingletonFile(String appName, String extension) {
        File newFile = null;
        // Acquire MD5
        try {
            String hash_text = generateHash(appName);
            newFile = new File(tmpDir + hash_text + "." + extension);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Couldn't generate hash for singleton file for " + appName, e);
        }

        // MD5 acquire fail
        if (newFile == null) {
            newFile = new File(tmpDir + appName + "." + extension);
        }
        return newFile;
    }

    private String generateHash(String key) throws NoSuchAlgorithmException {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        md.reset();
        return String.format("%032x", new java.math.BigInteger(1, md.digest(key.getBytes())));
    }
}
