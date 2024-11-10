package kv_store;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientBenchmark {

    private static int loops = 5000000;

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        List<Thread> thrds = new ArrayList<>();

        for (int i = 0; i < loops; i++) {
            Thread thr = new Thread(new Client());
            thr.start();
            thrds.add(thr);
        }

        try {
            for (Thread thr : thrds) {
                thr.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Record the end time
        long endTime = System.currentTimeMillis();

        // Calculate the elapsed time
        long elapsedTime = endTime - startTime;

        System.out.println("Total execution time: " + elapsedTime + " milliseconds");
    }
}