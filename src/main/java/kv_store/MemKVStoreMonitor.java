package kv_store;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.*;
import java.util.*;
import java.io.*;


public class MemKVStoreMonitor implements Runnable {

    KVStore kvStore = null;
    long maxMemSize;
    String dataFile;

    public MemKVStoreMonitor (KVStore kvStore) {
        this.kvStore = kvStore;
        this.maxMemSize = 32 * 1024; // 32KB
        this.dataFile = "/tmp/data";
    }

    @Override
    public void run() {
        while (true) {
            kvStore.writeLock.lock();

            while(getSerializedSize() < maxMemSize) {
                try {
                    kvStore.tooLarge.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile));
                oos.writeObject(kvStore.kvStore);
                kvStore.kvStore.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

            kvStore.writeLock.unlock();
        }
    }

    private long getSerializedSize() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(kvStore.kvStore);
            oos.close();
            return baos.toByteArray().length;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

}