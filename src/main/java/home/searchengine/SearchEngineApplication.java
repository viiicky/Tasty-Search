package home.searchengine;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import home.searchengine.models.Review;
import home.searchengine.services.FileDataReader;
import home.searchengine.services.Indexer;
import home.searchengine.services.TextFileDataReader;

@SpringBootApplication
public class SearchEngineApplication {

	// TODO: decide which all methods to make static later for all classes

	public static void main(String[] args) {
		SpringApplication.run(SearchEngineApplication.class, args);

		// load data from file
		FileDataReader fileDataReader = new TextFileDataReader();
		List<Review> reviewList = fileDataReader.deserializeData("src\\main\\resources\\foods.txt");    // TODO: Read from properties file

		Collections.shuffle(reviewList);	// might pass a custom source of randomness for better shuffling if needed
		int sampleSize = 100000;

		final Review[] reviews = new Review[sampleSize];
		Indexer indexer = new Indexer();
		Map<String, Set<Integer>> indexes = indexer.createIndexes(reviewList.subList(0, sampleSize).toArray(reviews));
	}
}
