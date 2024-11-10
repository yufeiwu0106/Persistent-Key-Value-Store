package kv_store;

import java.io.IOException;

public class Server {
    // static KVServer key_value_server = null;  // todo: implement kv server
    static KVSocketServer kv_socket_server = null;
    static KVStore kvStore = null;
    static String WALPath = "/tmp/wal";

    public static void main(String[] args) throws IOException {
        // System.out.printf("Binding server...")
        // socket_server = new SocketServer("localhost", 8080);

        // bind network handler
        // socket.addHandler(handler);
        // socket_server.connect();
        WALWriter walWriter = new WALWriter(WALPath);
        kvStore = new KVStore(walWriter);
        new Thread(new MemKVStoreMonitor(kvStore)).start();

        kv_socket_server = new KVSocketServer(
            8080,
            kvStore
        );

        System.out.printf(
            "Listening to port: %d\n", kv_socket_server.port
        );
        kv_socket_server.run();
    }
}