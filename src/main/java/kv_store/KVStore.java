package kv_store;

import java.util.HashMap;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.io.*;


public class KVStore {
    static TreeMap<String, String> kvStore = null;
    //

    static ReadWriteLock rwLock;
    static Lock readLock;
    static Lock writeLock;
    static WALWriter walWriter = null;
    static Condition tooLarge;
    static String dataFile;
    static long maxMemSize;

    public KVStore(WALWriter walWriter) {
        /**
         * This is not thread safe
         * NullPointerException or Index­OutOfBounds­Exception
         */
        this.kvStore = new TreeMap<String, String>();
        this.walWriter = walWriter;

        rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
        tooLarge = writeLock.newCondition();
        
        // dataFile = "/tmp/data";
        // maxMemSize = 32 * 1024;
        // writeToDisk();

        // new Thread(new MemKVStoreMonitor(this)).start();
        
        // this.kvStore = Collections.synchronizedMap(new HashMap<String, String>());
    }

    public void put(String key, String value) {
        writeLock.lock();
        walWriter.writeToFile("{key: " + key + ",value: " + value + "}");
        kvStore.put(key, value);

        if (getSerializedSize() > maxMemSize) {
            tooLarge.signal();
        }

        writeLock.unlock();
    }

    public String get(String key) {
        readLock.lock();
        try {
            return kvStore.get(key);
        } finally {
            readLock.unlock();
        }
        
    }

    public void delete(String key) {
        writeLock.lock();
        kvStore.remove(key);
        writeLock.unlock();
    }

    public boolean containsKey(String key) {
        readLock.lock();
        try {
            return kvStore.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    // private void writeToDisk() {
    //     // while (true) {
    //         writeLock.lock();

    //         while(getSerializedSize() < maxMemSize) {
    //             System.out.println("small data, don't flush");
    //             try {
    //                 tooLarge.await();
    //             } catch (InterruptedException e) {
    //                 Thread.currentThread().interrupt();
    //                 break;
    //             }
    //         }

            
    //         try {
    //             ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile));
    //             oos.writeObject(kvStore);
    //             kvStore.clear();
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }

    //         writeLock.unlock();
    //     // }
    // }

    private long getSerializedSize() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(kvStore);
            oos.close();
            return baos.toByteArray().length;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}