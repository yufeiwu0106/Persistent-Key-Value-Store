package kv_store;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class KVSocketServer {
    static ServerSocket serverSocket = null;
    int port;
    static KVStore kv_store = null;

    public KVSocketServer(int port, KVStore kv_store) {
        System.out.println("starting kv socket server");
        this.port = port;
        this.kv_store = kv_store;
    }

    void run() throws IOException {
        serverSocket = new ServerSocket(this.port);
        
        /**
         * Option 2: Maintains a thread pool
         */
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            16, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
        );
        
        // Running infinite loop for getting client request
        try {
            while (true) {
                // Socket object to receive incoming client requests
                Socket client = serverSocket.accept();

                System.out.println("New client connected " + client.getInetAddress().getHostAddress());

                // // create a new thread object
                // Handler clientSocket = new Handler(client, this.kv_store);
                // // MockHandler clientSocket = new MockHandler();

                // // This thread will handle the client separately
                // new Thread(clientSocket).start();

                /**
                 * Work with option 2
                 */
                threadPool.execute(new Handler(client, this.kv_store));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }
}

class MockHandler implements Runnable {

    @Override
    public void run() {
        System.out.println("hello world");
    }
}