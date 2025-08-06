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
3. After all the links on the page have been found, add them in order from first found to last found to the queue
4. Remove the first item of the queue (that is, the page we just checked), and repeat step 1 for the *new* first item in the queue.

The above procedure will run until the queue is empty.

Once the website has been traversed, the data will be saved to a MongoDB database. This program assumes a database
called `Indexer` has already been configured at address `mongodb://localhost:27017/`.

The data will be saved to a collection with a title of the provided start URL. The collection will contain objects representative
of the pages traversed. In addition to links, the pages' headings and title are saved. A reference count is also provided,
indicating how many times the page was linked to during the traversal.