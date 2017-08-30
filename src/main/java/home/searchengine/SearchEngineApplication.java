package home.searchengine;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import home.searchengine.models.Review;
import home.searchengine.services.FileDataReader;
import home.searchengine.services.TextFileDataReader;

@SpringBootApplication
public class SearchEngineApplication {

	// TODO: decide which all methods to make static later for all classes

	public static void main(String[] args) {
		SpringApplication.run(SearchEngineApplication.class, args);

		// load data from file
		FileDataReader fileDataReader = new TextFileDataReader();
		List<Review> reviewList = fileDataReader.deserializeData("src\\main\\resources\\foods.txt");    // TODO: Read from properties file
	}
}
