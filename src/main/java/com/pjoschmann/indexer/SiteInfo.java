package com.pjoschmann.indexer;

import java.util.ArrayList;

public class SiteInfo {
    // public record SiteInfo (String url, Integer referenceCount, String title, ArrayList<String> headings) {}

    final String url;
    int referenceCount = 0;
    String title;
    final ArrayList<String> headings = new ArrayList<>();

    public SiteInfo(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

    public ArrayList<String> getHeadings() {
        return headings;
    }

    public void addHeadings(ArrayList<String> headings) {
        this.headings.addAll(headings);
    }
}
