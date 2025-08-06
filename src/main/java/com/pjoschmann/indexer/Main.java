package com.pjoschmann.indexer;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        doSampleIndexing();
    }

    private static void doSampleIndexing() {
        WebCrawler wc = new WebCrawler();

        final String url = "https://misnymerch.com/";

        // Crawl site and find links
        HashMap<String, SiteInfo> siteMap;
        try {
             siteMap = wc.crawl(url, url); // Same URL for both
        }
        catch (IOException e) {
            System.out.println("Failed to traverse site: " + e.getLocalizedMessage());
            return;
        }

        // Create controller to work with database
        MongoDbController controller = new MongoDbController("mongodb://localhost:27017/",
                "Indexer");

        System.out.println("Creating collection for '" + url + "'.");
        // Create the collection
        controller.createCollection(url);

        System.out.println("Saving traversed site to the database...");
        // Save sites to that collection
        controller.saveToDb(siteMap, url);

        System.out.println("Done!");
    }
}