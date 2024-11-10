package kv_store;

import java.io.*;
import java.net.*;

public class KVMessage {
    private String reqType = null; // One of ["put", "get", "delete"]
    private String key = null;
    private String value = null;

    public final String getKey() {
		return key;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	public final String getValue() {
		return value;
	}

	public final void setValue(String value) {
		this.value = value;
	}

	public KVMessage(String reqType) throws IOException {
		this.reqType = reqType;
	}

    public final String toString() {
        return "{reqType: " + reqType + ", key: " + key + ", value: " + value + "}";
    }

    public byte getRequestType() {
        if(this.reqType.equals("put")) {
            return (byte) 'p';
        } else if (this.reqType.equals("get")) {
            return (byte) 'g';
        } else if (this.reqType.equals("delete")) {
            return (byte) 'd';
        }
        return (byte) 'f';  // should raise users errors
    }

    public void sendRequest(Socket socket) throws IOException {
        // Send request type;
        socket.getOutputStream().write(getRequestType());

        // Send key
        int keySize = this.key.length();
        byte keySizeInByte = (byte) keySize;
        
        socket.getOutputStream().write(keySizeInByte);
        socket.getOutputStream().write(this.key.getBytes("UTF-8"));

        if(this.reqType.equals("put")) {
            // Send value
            short valueSize = (short) this.value.length();
            byte[] valueSizeBytes = new byte[2];
            valueSizeBytes[0] = (byte) (valueSize >> 8);
            valueSizeBytes[1] = (byte) valueSize;
            socket.getOutputStream().write(valueSizeBytes);
            socket.getOutputStream().write(this.value.getBytes("UTF-8"));
        }

    }

}