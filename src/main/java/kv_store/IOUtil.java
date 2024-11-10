package kv_store;

import java.io.*;
import java.net.*;

public class IOUtil {

    public static void readArray(InputStream s, byte[] buffer) throws IOException {
        int n = s.readNBytes(buffer, 0, buffer.length);
        if (n != buffer.length) {
            throw new IOException (
                "Failed to read full message size " + buffer.length + " bytes. Only read " + n + " bytes"
            );
        }
    }

    public static byte[] readVariableArray(InputStream s) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] data = new byte[1024];
        int bytesRead;
        while ((bytesRead = s.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        return buffer.toByteArray();

    }

}