package com.pjoschmann.indexer;

import java.io.IOException;
import java.util.HashMap;

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