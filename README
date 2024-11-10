Key Value Store
===================

# Scope
* Single node key value store
* supports put, get, delete
* Be able to recover from machine root

# System Requirements

* Functional requirements (API)
  * void put(String key, String value)
  * String get(String key)

(option: key and value can be just random length bytes arrary; perform serialization and deserialization are needed)

* Non-functional requirements
  * Performant/low e2e latency: needs concurrency (multithreads)
  * index: 
  * Durable/persistent: persist the data on disk
  * Consistency: data corruption awareness; CRC checksum

* System specs
  * Hardware spec: 32 CPU cores, 32GB RAM, 1TB disk size
  * workload/client query pattern: write heavy, read heavy, general
  * Depends on 1) the protocol we use to communicate between client & server 2) how do we store key value pair
    * QPS 50K/s -> 50K/s * 1103 byte network throughput ~55MB/s
    * Number of key value pairs stored in our system (?)

# Components

* client & server communication protocol
  * server: listens to port (8080) and wait for incoming request; upon every request,
    * option 1: create a thread that handles the socket and receive from/responde to client
    * option 2: Maintains a thread pool. Pick one idle thread to work on the request
  * client: Send packet to server
  * Message: req type (1 byte) + key size (1 byte) + key (<= 100 bytes) + value (2 bytes) + values (<= 1000 bytes) <= 1104 bytes
  * Thread pool essentially is shared object that coordinates concurrent threads by condition variables

```java
/*
* server socke that maintains thread pool and run incoming request as a thread
*/
public class KVServerSocket {

  static ServerSocket serverSocket = null;

  void run() {
    Socket socket = serverSocket.accpet();
    
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        16, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
    );

    while (true) {
      Socket client = socket.accept();

      threadPool.execute(new Handler(client))
      
    }
  }
}

/**
 * This class takes a user request and run as a thread
 * It takes socket and kv store in constructor and call specific
 * API depending on users requests
 */
public class Handler implements Runnable {
  Socket clientSocket = null;

  public Handler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  @Override
  public void run() {
    /**
     * 1. Deserialzing/decoding the input stream and convert it to the actual request
     * 
     */
     reqType = getRequestType();

     if reqType.equals("p") {
      put(getKey(), getValue()); // Get key, value from input stream
     } else if reqType.equals("g") {
      get(getKey());
     } else if reqType.equals("d") {
      delete(getKey());
     }
  }
}
```

Thread pool basic principals: maintains a list of thread and use linked list to store pending jobs; thread will try to get job and it will be blocked in waiting state until a job shows up in the linked list and gets popped

* how do we store data in memory
Needs a data structure to store data:
  * HashTable like data structure
    * HashMap with locks
    * Custom hash table with hash function & hash scheme (cuckoo hasing)
    * reuse some language specific data structure 
  * Tree based data structure; Make it sorted by key
    * B+ tree
    * Red black tree (TreeMap) not thread safe; infinite loop
  * Data needs to be flushed to disk when it hits a pre-defined size
    * Periodic memory monitor running as a thread
    * Upon write, performs memory check. This requires efficient memory management and tracker.

```java
class KVStore {
  static TreeMap<String, String> kvStore = null;
  static WALWriter walWriter = null;

  // Read write lock
  static Lock readLock = null;
  static Lock writeLock = null;
  static tooLarge = writeLock.newCondition();

  public KVStore (WALWriter walWriter) {
    this.kvStore = new TreeMap<>();
    this.walWriter = walWriter;
  }

  public void put(String key, String value) {
    writeLock.lock();
    walWriter.write(); // Durable
    kvStore.put(key, value);
    if (memSize > threshold) {
        tooLarge.signal();
    }
    writeLock.unlock();
  }

  public get(String key) {
    readLock.lock();
    String val = kvStore.get(key);
    readLock.unlock();
  }

  //n delete is like put with writeLock
}
```

```java
class MemMonitor implements Runnable {
    public MemMonitor(KVStore kvStore) {
        this.kvStore = kvStore;
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

            Flush();
            
            kvStore.writeLock.unlock();
        }
    }
}
```

* how do we store data on disks
  * data file
  * WAL
  * FileManager: this is the class that writes to WAL; 

```java
public class WALWriter {
  String filePath = "";
  static Lock lock = null;

  public void write(String log) {
    lock.lock();
    // write to file. Ensure to use the API and setup to write the log to disk, not page cache
    lock.unlock();
  }
}
```

* Concurrency control

* index

* partitioning by hashing key

... (tbd)

# Shared objects
  * KVStore: for concurrent key value store read write
  * WALWriter: for concurrent write to wal log
  * MemKVStoreMonitor: for flush data from memory to disk

# Multithread primitive

start():

join():

yield(): a low pri thread that can make progress yield to high pri thread

wait(): wait for condition variable; this should replace sleep()

signal(): signal one thread on the same cv

broadcast(): signal all threads on the same cv