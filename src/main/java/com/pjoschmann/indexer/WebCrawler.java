package com.pjoschmann.indexer;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class WebCrawler implements Crawler<SiteInfo> {

    private record SiteContent (String title, ArrayList<String> headings, ArrayList<String> links) {}
    private final Logger logger = Logger.getLogger(WebCrawler.class.getName());


    @Override
    public HashMap<String, SiteInfo> crawl(final String startUrl, final String rootUrl) throws IOException {

        return traverseSite(startUrl, rootUrl);
    }

    /**
     * <p>Traverses the website for links.</p>
     *
     * <p>This method will traverse the website starting at startUrl using BFS. This will continue until all reachable
     * links have been covered. The links will be stored in a hashmap for easy access to content with the URL.</p>
     *
     * <p>It may be useful to extract just the SiteInfo nodes from the hashmap, if only content is needed.</p>
     * @param startUrl Where to begin the traversal
     * @param rootUrl The root of the tree
     * @return Hashmap, with the item's URL as the key and its SiteInfo content as its value
     * @throws IOException If an unexpected exception occurs, pass it along!
     */
    private HashMap<String, SiteInfo> traverseSite(final String startUrl, final String rootUrl) throws IOException {

        // Initialize with first node
        // The node queue stores URL Strings. The site map uses these URL Strings as keys.
        ArrayList<String> nodeQueue = new ArrayList<>(); // Used for site traversal
        HashMap<String, SiteInfo> siteMap = new HashMap<>(); // Used for saving site data

        siteMap.put(startUrl, new SiteInfo(startUrl, ""));
        nodeQueue.add(startUrl);

        while (!nodeQueue.isEmpty()) {

            // Get site content for element at start of queue
            SiteContent siteContent = getSiteContent(nodeQueue.getFirst(), rootUrl);

            // Save site content to first node in queue (representative of the current site)
            siteMap.get(nodeQueue.getFirst()).setTitle(siteContent.title);
            siteMap.get(nodeQueue.getFirst()).addHeadings(siteContent.headings);

            // Add newly found links to the site map and node queue
            for (String link : siteContent.links) {

                // If the link isn't already in the site map, add it, and add to queue for later traversal
                if (!siteMap.containsKey(link)) {
                    siteMap.put(link, new SiteInfo(link, ""));
                    nodeQueue.add(link);
                }

                // Otherwise, increment that link's reference count
                else {
                    siteMap.get(link).setReferenceCount(siteMap.get(link).getReferenceCount()+1);
                }
            }

            nodeQueue.removeFirst();

            // Sleep for 1 second before continuing to avoid DoS-ing the site
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                logger.warning("Thread sleep interrupted, returning incomplete sitemap: " +
                        e.getLocalizedMessage());
                return siteMap;
            }

        }

        return siteMap;

    }

    /**
     * <p>
     * Gets relevant site content and returns it as a SiteContent record containing the site's title, the text of
     * its headings, and all links.
     * </p>
     * <p>
     * This method aims to extract relevant information from the site such that information on what the site contains
     * can be derived without needing to save the entire HTML file.
     * </p>
     * @param url URL of the site to connect to
     * @param rootUrl Root URL, in case the site uses links relative to its own file system (starting with "/")
     * @return SiteContent record containing the site's title, headings, and links
     * @throws IOException If an unexpected exception occurs, pass it along!
     */
    private SiteContent getSiteContent(final String url, final String rootUrl) throws IOException {

        System.out.println("Attempting to get site content for: '" + url + "'.");

        Document doc = null;
        final String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/136.0.7103.48 Safari/537.36";
        String title = "";
        ArrayList<String> headings = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();

        for (int i=0; i<5; i++) {
            try {
                doc = Jsoup.connect(url).userAgent(userAgent).get();
                break;
            }
            catch (HttpStatusException | UnsupportedMimeTypeException e) {
                System.out.println("Can't connect to '" + url + "' due to " + e.getMessage());
                return new SiteContent(title, headings, links); // Blank!
            }
            catch (SocketTimeoutException e) {
                System.out.println("Failed to connect to '" + url + "' due to timeout - Trying again...");
                // Wait a little bit if we get a timeout
                try {
                    Thread.sleep(3000);
                }
                catch (InterruptedException f) {
                    return new SiteContent(title, headings, links);
                }
            }
        }

        if (doc == null) {
            System.out.println("Failed to connect to '" + url + "'. Skipping...");
            return new SiteContent(title, headings, links);
        }


        // Get title
        title = doc.title();

        ArrayList<Element> elements = doc.getAllElements();

        for (Element el : elements) {

            // Get links
            if (el.tag().toString().equals("a")) {
                String hrefLink = transformLink(el.attr("href"), url, rootUrl);

                if (hrefLink.startsWith(rootUrl)) {
                    links.add(hrefLink);
                }
            }

            // Get headings
            else if (el.tag().toString().matches("h\\d")) {
                headings.add(el.text());
            }
        }

        return new SiteContent(title, headings, links);
    }

    /**
     * <p>Transforms a link into a usable format</p>
     * <p>
     * This method will transform link shorthand and relative links into full-domain links. Specifically,
     * it will:
     * <ul>
     *     <li>Replace "//" with "https://</li>
     *     <li>Replace "/" with the root URL</li>
     *     <li>Replace "./" with the current URL</li>
     *     <li>Remove trailing "/", if the link happens to end with one</li>
     * </ul>
     * </p>
     * @param link The link to transform
     * @param currentUrl The current URL containing the link (in the case of "./")
     * @param rootUrl The website's root URL (in the case of "/")
     * @return String containing a link usable for traversing the website
     */
    private static String transformLink(String link, String currentUrl, String rootUrl) {

        StringBuilder linkSb = new StringBuilder(link);

        // Replace "//" with "https"
        if (linkSb.length() >= 2 && linkSb.substring(0,2).equals("//")) {
            linkSb.replace(0,2, "https://");
        }

        // Replace "/" with root URL
        else if (!linkSb.isEmpty() && linkSb.charAt(0) == '/') {
            linkSb.replace(0,1,rootUrl + "/");
        }

        // Replace "./" with current URL
        else if (linkSb.length() >= 2 && linkSb.substring(0,2).equals("./")) {
            linkSb.replace(0,2,currentUrl);
        }

        // Remove trailing "/" if that's there
        if (!linkSb.isEmpty() && linkSb.charAt(linkSb.length()-1) == '/') {
            linkSb.delete(linkSb.length()-1, linkSb.length());
        }

        return linkSb.toString();
    }

}
