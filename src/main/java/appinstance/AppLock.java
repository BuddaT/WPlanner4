package appinstance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 * The Class AppLock.
 *
 * Adapted from rumatoest:
 * @url http://nerdydevel.blogspot.com/2012/07/run-only-single-java-application-instance.html
 * 
 * This class is not thread-safe.
 */
public final class AppLock {
    /** The lock_file. */
    private final File lockFile;

    /** The lock. */
    private final FileLock lock;
 
    /** The lock_channel. */
    private final FileChannel lockChannel;
 
    /** The lock_stream. */
    private final FileOutputStream lockStream;
    
    private final static Logger LOGGER = Logger.getLogger(AppLock.class.getName());

    /**
     * Instantiates a new app lock.
     *
     * @param key Unique application key
     * @throws Exception The exception
     */
    private AppLock(String key) throws Exception {
        lockFile = new SingletonFileBuilder().buildSingletonFile(key, "app_lock");

        lockStream = new FileOutputStream(lockFile);
 
        String f_content = "Java AppLock Object\r\nLocked by key: " + key
            + "\r\n";
        try {
            lockStream.write(f_content.getBytes());
        } catch (IOException e) {
            throw new FileLockException(e);
        }
 
        lockChannel = lockStream.getChannel();
 
        lock = lockChannel.tryLock();
 
        if (lock == null) {
            throw new FileLockException("Can't create Lock");
        }
    }
 
    /**
     * Release Lock.
     * Now another application instance can gain lock.
     *
     * @throws Throwable
     */
    private void release() throws Throwable {
        if (lock.isValid()) {
                lock.release();
        }
        if (lockStream != null) {
                lockStream.close();
        }
        if (lockChannel.isOpen()) {
                lockChannel.close();
        }
        if (lockFile.exists()) {
                lockFile.delete();
        }
    }
 
    @Override
    protected void finalize() throws Throwable {
        this.release();
        super.finalize();
    }
 
    /** The instance. */
    private static AppLock instance;
 
    /**
     * Set application lock.
     * Method can be run only one time per application.
     * All next calls will be ignored.
     *
     * @param key Unique application lock key
     * @return true, if successful
     */
    public static boolean setLock(String key) {
        if (instance != null) {
            return true;
        }
 
        try {
            instance = new AppLock(key);
        } catch (FileLockException e) {
            instance = null;
            LOGGER.log(Level.INFO, "Failed to set AppLock, couldn't lock or write to lock file", e);
            return false;
        } catch (Exception ex) {
            instance = null;
            Logger.getLogger(AppLock.class.getName()).log(Level.SEVERE, "Failed to set AppLock", ex);
            return false;
        }
 
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                AppLock.releaseLock();
            }
        });
        return true;
    }
 
    /**
     * Trying to release Lock.
     * After release you can not user AppLock again in this application.
     */
    public static void releaseLock() {
        try {
            if (instance == null) {
                throw new NoSuchFieldException("Instance is null");
            }
            instance.release();
        } catch (Throwable ex) {
            Logger.getLogger(AppLock.class.getName()).log(Level.SEVERE, "Fail to release", ex);
        }
    }
    
    private static class FileLockException extends Exception {
        private static final long serialVersionUID = 3902356910905874866L;

        FileLockException(Throwable cause) {
            super(cause);
        }
        
        FileLockException(String message) {
            super(message);
        }
    }
}