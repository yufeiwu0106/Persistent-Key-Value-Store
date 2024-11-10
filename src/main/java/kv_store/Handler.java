package kv_store;

import java.net.Socket;

import java.io.*;
import java.net.*;

/**
 * Each handler will be used for each separate thread for each request
 * This is done by implementing Runnable so it can be passed as a runnable
 * target while creating a new Thread
 */
public class Handler implements Runnable {
    private final Socket clientSocket;
    private final KVStore kvStore;

    public Handler(Socket socket, KVStore kvStore) {
        this.clientSocket = socket;
        this.kvStore = kvStore;
    }

    @Override
    public void run() {
        _run();
    }

    public void _run() {
        try {
            InputStream inputStream = clientSocket.getInputStream();

            // Request type
            // String reqType = String.valueOf((char) inputStream.read());

            String result = processRequest(inputStream);
            clientSocket.getOutputStream().write(result.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                // inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String processRequest(InputStream s) {
        try {
            String reqType = String.valueOf((char) s.read());

            System.out.println("[" + Thread.currentThread().getName() + "] Request type: {" + reqType + "}");
            
            if (reqType.equals("p")) {
                processPut(s);
                return "GG_SUCCESS";
            } else if (reqType.equals("g")) {
                return processGet(s);
            } else if (reqType.equals("d")) {
                processDelete(s);
                return "GG_SUCCESS";
            }
            return "REQUEST_TYPE_UNKNOWN";
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return "REQUEST_TYPE_UNKNOWN";
        }
    }

    private void processPut(InputStream s) throws IOException {
        String k = decodeKey(s);
        String v = decodeValue(s);
        kvStore.put(k, v);

        System.out.println("Updated {key: " + k + ", value: " + v + "}");
    }

    private String processGet(InputStream s) throws IOException {
        String k = decodeKey(s);

        System.out.println("Retrive value for {key: " + k + "}");
        
        if (kvStore.containsKey(k)) {
            return kvStore.get(k);
        } else {
            return "";
        }
    }

    private void processDelete(InputStream s) throws IOException {
        String k = decodeKey(s);

        System.out.println("Remove value for {key: " + k + "}");

        kvStore.delete(k);
    }

    private String decodeKey(InputStream s) throws IOException {
        // Received key
        short keySize = (short) s.read();
        byte[] keyBuffer = new byte[keySize];
        IOUtil.readArray(s, keyBuffer);

        String key = new String(keyBuffer, "UTF-8");
        return key;
    }

    private String decodeValue(InputStream s) throws IOException {
        // Received value
        byte high = (byte) s.read();
        byte low = (byte) s.read();
        
        short valueSize = (short) (high << 8 | low & 0xFF);
        byte[] valueBuffer = new byte[valueSize];
        IOUtil.readArray(s, valueBuffer);
        String value = new String(valueBuffer, "UTF-8");

        return value;
    }

}