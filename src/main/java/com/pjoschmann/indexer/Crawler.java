package com.pjoschmann.indexer;

import java.io.IOException;
import java.util.HashMap;

public interface Crawler<T> {

    /**
     * The crawl method will traverse the system in attempt to find all
     * items inside of it.
     * @return HashMap with the key being the item's identifier and T being a representation of that item
     * @param startUrl Where the traversal should begin
     * @param rootUrl The root of the tree.
     */
    HashMap<String, T> crawl(final String startUrl, final String rootUrl) throws IOException;

}
