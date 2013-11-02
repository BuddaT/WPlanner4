package appinstance;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

/**
 * Monitors the input file that tells us when a new instance has requested that we open a file.
 */
public class FileListener extends TailerListenerAdapter {
    private static final int QUEUE_SIZE = 10;

    private static final int POLLING_INTERVAL = 300;

    private static final int MAX_DELETE_TIMEOUT = 1000;

    private final BlockingQueue<String> files = new ArrayBlockingQueue<String>(QUEUE_SIZE);

    private final File listenFile;

    private final ReentrantLock lock = new ReentrantLock();

    private Tailer tailer;

    private boolean isListening = false;

    private final CountDownLatch deleteSignal = new CountDownLatch(1);

    public FileListener(String key) throws FileListenerException {
        listenFile = new SingletonFileBuilder().buildSingletonFile(key, "listen");

        try {
            if (!(listenFile.createNewFile() ||
                    listenFile.delete() && listenFile.createNewFile())) {
                throw new FileListenerException("Couldn't create listening file " + listenFile.getName() + ": file already exists");
            }
        } catch (IOException e) {
            throw new FileListenerException("Error creating listening file " + listenFile.getName(), e);
        }
    }

    public void listen() {
        lock.lock();
        try {
            System.out.println("Listening to file " + listenFile.getName());
            if (isListening) {
                throw new IllegalStateException("Already listening to message file");
            }
            // Start it manually as a thread so that we can detect when it's finished running and the file is no longer
            // in use
            tailer = new Tailer(listenFile, this, POLLING_INTERVAL);
            Thread thread = new Thread(new TailerRunner());
            thread.start();
            isListening = true;
        } finally {
            lock.unlock();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                lock.lock();
                try {
                    if (isListening) {
                        tailer.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isListening = false;
                    lock.unlock();
                }
                try {
                    deleteSignal.await(MAX_DELETE_TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ignored) {
                }
                System.out.println("Attempting to delete listen file " + listenFile.getName());
                listenFile.delete();
            }
        });
    }

    @Override
    public void handle(String s) {
        System.out.println("File appended: " + s);
    }

    @Override
    public void handle(Exception e) {
        e.printStackTrace();
    }

    // Runs the tailer so that the delete can be signalled once it's no longer running
    private class TailerRunner implements Runnable {
        @Override
        public void run() {
            tailer.run();
            deleteSignal.countDown();
        }
    }
}
