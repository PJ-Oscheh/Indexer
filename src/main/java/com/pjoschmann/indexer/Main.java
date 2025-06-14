package com.pjoschmann.indexer;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        WebCrawler wc = new WebCrawler();

        try {
            wc.crawl("https://ezbatteryreconditioning.com", "https://ezbatteryreconditioning.com");
        }
        catch (IOException e) {
            System.out.println("Failed to traverse site: " + e.getLocalizedMessage());
        }
    }
}