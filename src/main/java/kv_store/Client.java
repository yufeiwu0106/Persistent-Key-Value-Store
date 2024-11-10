package kv_store;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client implements Runnable {
    private Socket socket = null;

    private static String[] keyLst = {
        "foo", "bar", "baz", "wtf", "wth", "plz", "ffs", "qps", "databricks", "onsite", "interview", "system design"
    };

    private static String[] valLst = {
        "not bad", "8 out of 10", "never look back", "IDGAF", "SZA concert"
    };

    private static String[] reqTypes = {
        "p", "g", "d"
    };

    public static String getRandomKey() {
        Random random = new Random();
        int randomIndex = random.nextInt(keyLst.length);
        return keyLst[randomIndex];
    }

    public static String getRandomVal() {
        Random random = new Random();
        int randomIndex = random.nextInt(valLst.length);
        return valLst[randomIndex];
    }

    public static String getRandomReqType() {
        Random random = new Random();
        int randomIndex = random.nextInt(reqTypes.length);
        return reqTypes[randomIndex];
    }

    static String getRandomId() {
        Random random = new Random();
        int randomInt = random.nextInt();
        return Integer.toString(randomInt);
    }

    public void put(String key, String value) {
        // Send Quest
        try {
            Socket socket = new Socket("localhost", 8080);

            KVMessage message = new KVMessage("put");
            message.setKey(key);
            message.setValue(value);
            message.sendRequest(socket);

            // Get Response
            InputStream inputStream = socket.getInputStream();
            // byte[] respBuffer = IOUtil.readVariableArray(inputStream);

            byte[] respBuffer = new byte[1];
            IOUtil.readArray(inputStream, respBuffer);

            String response = new String(respBuffer, "UTF-8");
            System.out.println("Request: " + message.toString() + ". Response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void get(String key) {
        // Send Quest
        try {
            Socket socket = new Socket("localhost", 8080);

            KVMessage message = new KVMessage("get");
            message.setKey(key);
            message.sendRequest(socket);

            // Get Response
            InputStream inputStream = socket.getInputStream();
            byte[] respBuffer = new byte[1];
            IOUtil.readArray(inputStream, respBuffer);
            String response = new String(respBuffer, "UTF-8");

            System.out.println("Request: " + message.toString() + ". Response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void delete(String key) {
        // Send Quest
        try {
            Socket socket = new Socket("localhost", 8080);

            KVMessage message = new KVMessage("delete");
            message.setKey(key);
            message.sendRequest(socket);

            // Get Response
            InputStream inputStream = socket.getInputStream();
            byte[] respBuffer = new byte[1];
            IOUtil.readArray(inputStream, respBuffer);

            String response = new String(respBuffer, "UTF-8");
            System.out.println("Request: " + message.toString() + ". Response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        String reqType = getRandomReqType();
        String key = getRandomKey() + getRandomId();

        if (reqType.equals("p")) {
            put(key, getRandomVal());
        } else if (reqType.equals("g")) {
            get(key);
        } else if (reqType.equals("d")) {
            delete(key);
        } else {
            System.out.println("NOP");
        }
    }
}