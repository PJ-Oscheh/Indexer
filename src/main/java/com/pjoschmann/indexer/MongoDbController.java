package com.pjoschmann.indexer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.logging.Logger;

public class MongoDbController implements NoSqlDbController<SiteInfo> {

    private final String connectionString;
    private final String databaseName;
    private final Logger logger = Logger.getLogger(MongoDbController.class.getName());

    public MongoDbController(String connectionString, String databaseName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
    }

    @Override
    public void createCollection(String rootUrl) {

        // Get database
        try (MongoClient mongoClient = MongoClients.create(connectionString)){
             MongoDatabase db = mongoClient.getDatabase(databaseName);

            // Check if collection already exists
            if (checkCollectionExists(db, rootUrl)) {
                // Skip if so
                logger.warning("Collection " + rootUrl + " exists - not adding to database.");
                return;
            }

            // Add collection to database
            db.createCollection(rootUrl);
        }
        catch (IllegalArgumentException e) {
            logger.warning("Failed to get database '" + databaseName +"'.");
            throw e;
        }
    }

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public void saveToDb(HashMap<String, SiteInfo> siteMap, String collectionName) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase db = mongoClient.getDatabase(databaseName);

            MongoCollection<Document> collection = db.getCollection(collectionName);

            for (SiteInfo siteInfo : siteMap.values()) {
                Document doc = new Document("_id", new ObjectId())
                        .append("title", siteInfo.getTitle())
                        .append("url", siteInfo.getUrl())
                        .append("headings", siteInfo.getHeadings())
                        .append("referenceCount", siteInfo.getReferenceCount());

                collection.insertOne(doc);
            }
        }
    }

    private boolean checkCollectionExists(MongoDatabase database, String collectionName) {
        for (String collection : database.listCollectionNames()) {
            if (collectionName.equals(collection)) {
                return true;
            }
        }
        return false;
    }
}
