package com.pjoschmann.indexer;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        indexEvilBatteryScamWebsite();
    }

    private static void indexEvilBatteryScamWebsite() {
        WebCrawler wc = new WebCrawler();

        final String url = "https://ezbatteryreconditioning.com";

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

        // Create the collection
        controller.createCollection(url);

        // Save sites to that collection
        controller.saveToDb(siteMap, url);

    }
}