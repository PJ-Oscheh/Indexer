package com.pjoschmann.indexer;

import java.io.IOException;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Main {
    public static void main(String[] args) {

        WebCrawler wc = new WebCrawler();

        try {
            HashMap<String, SiteInfo> siteMap = wc.crawl("https://ezbatteryreconditioning.com",
                    "https://ezbatteryreconditioning.com");
        }
        catch (IOException e) {
            System.out.println("Failed to traverse site: " + e.getLocalizedMessage());
        }


    }
}