# Indexer

A program to index all pages of a website.

## Usage

To index a webpage, simply run the program and enter your favorite URL. For example:

`Enter a URL to scrape: https://misnymerch.com/`

This will scrape [https://misnymerch.com](https://misnymerch.com).

## Implementation

The website is traversed using the BFS algorithm, meaning:

1. Start at the top page (that is, the URL provided) and at it to a queue
2. Search the page for links and take note of them
3. After all the links on the page have been found, add them to the queue in order from first found to last found.
4. Remove the first item of the queue (that is, the page we just checked), and repeat step 1 for the *new* first item in the queue.

The above procedure will run until the queue is empty.

Once the website has been traversed, the data will be saved to a MongoDB database. This program assumes a database
called `Indexer` has already been configured at address `mongodb://localhost:27017/`.

The data will be saved to a collection with a title of the provided start URL. The collection will contain objects representative
of the pages traversed. In addition to links, the pages' headings and title are saved. A reference count is also provided,
indicating how many times the page was linked to during the traversal.

## Dependencies
This program has two dependencies: JSoup and the MongoDB Java Driver.

### JSoup

**Version: 1.20.1**

[JSoup](https://jsoup.org/) is a wonderful library that assists in parsing HTML content. It's primary use case in this program
is to ease the process of iterating through HTML elements and to pick out links and headers. It also provides an implementation
for connecting to a URL and downloading its contents with a supplied user agent.

### MongoDB Driver

**Version: 5.5.1**

[The MongoDB Java Driver](https://www.mongodb.com/docs/drivers/java/sync/current/get-started/#std-label-java-get-started)
provides a means to interact with a MongoDB NoSQL database from Java code. Note that MongoDB offers two variants of
the driver: sync and async. This program uses the former as it's relatively simple in scope; it only performs one crawl
at a time.