package com.pjoschmann.indexer;

import java.util.HashMap;


/**
 * Provides implementation for working with Document-style NoSQL databases
 * @param <T>
 */
public interface NoSqlDbController<T> {

    /**
     * Creates a new NoSQL Collection
     * @param rootUrl Root URL for the collection's name
     */
    void createCollection(String rootUrl);

    /**
     * Saves provided site map to the database
     * @param siteMap Map of a crawled system (like a website or filesystem)
     * @param collectionName Name of collection to save to
     */
    void saveToDb(HashMap<String, T> siteMap, String collectionName);

    /**
     * Gets the connection string
     * @return The connection string
     */
    String getConnectionString();

    String getDatabaseName();
}
