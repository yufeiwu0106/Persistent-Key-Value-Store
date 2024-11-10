package kv_store;

/***
 * This is the simple interface that all of the KeyValue servers, caches and stores
 * should implement.
 */
public interface KeyValueInterface {
    /**
     * Insert Key, Value pair into the storage unit
     * @param key is the object used to index into the store
     * @param value is the object correposnding to a unique key
     * @throws IOException is thrown when there is an error when inserting the entry into the store
     * 
     * @TODO can we not assume value can only be string?
     */
    public void put(String key, String value) throws Exception;

    /**
     * Retrieve the object corresponding to the provided key
     * @param key is the object used to into the store
     * @return the value corresponding to the provided key
     * @throws KVException if there is an error when looking up the object store
     */
    public String get(String key) throws Exception;

    /**
    * Delete the object corresponding to the provided key 
    * @param key is the object used to index into the store
    * @throws KVException if there is an error when looking up the object store
    */     
    public void del(String key) throws Exception; 
}