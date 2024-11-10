package kv_store;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class WALWriter {
    private final String filePath;
    private final ReentrantLock lock;

    public WALWriter(String filePath) {
        this.filePath = filePath;
        this.lock = new ReentrantLock();
    }

    public void writeToFile(String content) {
        // Acquire the lock
        lock.lock();
        try {
            try (FileWriter writer = new FileWriter(filePath, true)) {
                writer.write(content);
                writer.write(System.lineSeparator()); // Optionally, add a newline
            } catch (IOException e) {
                e.printStackTrace(); // Handle or log the exception
            }
        } finally {
            // Release the lock in a finally block to ensure it's always released
            lock.unlock();
        }
    }
}