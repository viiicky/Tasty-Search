# Tasty-Search

A search engine to search gourmet food reviews data and return the top K reviews that
have the highest overlap with the input query.

## What you’ll need to run this application:
* Dataset: http://snap.stanford.edu/data/webFineFoods.html Alternate link: https://drive.google.com/file/d/0B8_VSW2-5XmpSTNlZXV4cVdLRUE/view
* Maven 3.0+ to install dependecies required by application: https://maven.apache.org/download.cgi
* Java to run the application: http://www.oracle.com/technetwork/java/javase/downloads/index.html

## Running the application:
* Prepare dataset: Extract the dataset and save it as `.txt` file
* Get the source: Run `git clone https://github.com/viiicky/Tasty-Search.git`
* Create the jar: Navigate to the root of source where `pom.xml` exists and give command `mvn package`
This will compile the code and package it creating `search-engine-0.0.1-SNAPSHOT.jar` in `target` directory
and a BUILD SUCCESS message will appear.
* Run the application: Give command `java -jar target/search-engine-0.0.1-SNAPSHOT.jar --dataset="<path to extracted data file with extension>"` to run the application.
You should see the Tasty Search! banner and a lot of logs that ends with something `Started SearchEngineApplication...`

That's it. The application is now up and running

## Checkout the service:
* The service expects a `GET` request with search tokens passed in the query params. There is no limit on the number of search tokens.
Say you want to serach for good dog foods, just open any browser and hit: `localhost:8080/reviews?token1=good&token2=dog&token3=food`.
This will fetch the top 20 highest scoring reviews.
Response will be unstructured. To be able to read it copy the entire response in the textbox at https://jsonlint.com/ and click on Validate JSON button.

* Alternatively, you can use Postman application to check the service out which provides easy option to pass query params and will show the response in a proper structure.
Note that the token name can be anything i.e. if you want to serch for quality cat foods, you can hit: `localhost:8080/reviews?t4=quality&t5=cat&t6=food`
Notice `t`x instead of `token`x. Token name can be anything, only values matter. Just don't repeat a token in a single request.

## What it does:
The simple explanation to how the service works is as follows:
* It reads the datafile given at the time of running the application and randomly samples 100K reviews from the set for the index. If the datafile has less than 100K of reviews it throws an exception and the application stops.
* If any search is made while the indexing is still in progress and exception is thrown mentioning to try after some-time and the indexing conitnues.
* Indexing is done for the `summary` and `text` field of a review document. Once the indexing finishes the application becomes ready to serve the search queries.
* For each search query made, reviews are searched and ranked.
* Rank of a review = # of unique tokens matching between input query and review document (summary and text)
* Tie between two reviews is resolved using `score` field present in the review document. If the scores are also same any aritrary order is taken.
* No review documents with 0 rank are entertained.
* Case insensitive i.e. `dog`, `Dog`, `doG` all are treated as a single term while indexing as well as while searching.
* Takes care of whitespaces, symbols, puntuations etc. while indexing.

## What it does not do:
* No spell correction for the input tokens.
* Plurals and singulars are considered as differnt token i.e. `food` and `foods` are treated as two different terms both while indexing as well as searching.

## Future work:
* Introduce parallelization for bulk processes.
* Interface out storage/access task from services.
* Load test and measure performance. Try different approaches here. Specially parallelized and sequential.
