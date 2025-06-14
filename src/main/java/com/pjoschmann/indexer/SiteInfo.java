package com.pjoschmann.indexer;

import java.util.ArrayList;

public class SiteInfo {

    private final String url;
    private int referenceCount = 0;
    private String title;
    private final ArrayList<String> headings = new ArrayList<>();

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
