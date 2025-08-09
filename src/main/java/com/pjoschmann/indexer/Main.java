package com.pjoschmann.indexer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@RestController
@SpringBootApplication
public class Main {

    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    @GetMapping("/indexSite")
    ResponseEntity<Map<String,SiteInfo>> indexSite(@RequestBody final String rawJson) {

        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        Map<String,Object> parsedJson = jsonParser.parseMap(rawJson);

        final String url = parsedJson.get("url").toString();

        WebCrawler wc = new WebCrawler();

        // Crawl site and find links
        HashMap<String, SiteInfo> siteMap = new HashMap<>();

        try {
            siteMap = wc.crawl(url, url);
        }
        catch (IOException e) {
            System.out.println("Failed to traverse site: " + e.getLocalizedMessage());
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(siteMap);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
//        System.out.print("Enter a URL to scrape: ");
//        Scanner scanner = new Scanner(System.in);
//        String url = scanner.nextLine();
//
//        doIndexing(url);
    }

    /**
     * <p>Indexes a webpage for the provided URL.</p>
     * <p></p>
     * This method will use a WebCrawler to traverse a page starting at
     * the provided URL. Once that's complete, it'll save each discovered page's title,
     * heading, and reference count (that is, the amount of times other pages linked to that page)
     * to a MongoDB database.
     * </p>
     * @param url URL from which to begin crawling
     */
    private static void doIndexing(final String url) {
        WebCrawler wc = new WebCrawler();

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