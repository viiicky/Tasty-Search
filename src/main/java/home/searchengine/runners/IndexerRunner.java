package home.searchengine.runners;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import home.searchengine.models.Review;
import home.searchengine.services.indexer.Indexer;
import home.searchengine.services.reader.FileDataReader;

@Component
public class IndexerRunner implements ApplicationRunner {

	private final String filePath;

	private final int sampleSize;

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexerRunner.class);

	private final FileDataReader reader;

	private final Indexer indexer;

	@Autowired
	public IndexerRunner(
			@Value("${file-name}") final String filePath,
			@Value("${sample-size}") final int sampleSize,
			@Qualifier("textFileDataReader") final FileDataReader reader,
			@Qualifier("sequentialIndexer") final Indexer indexer) {

		this.filePath = filePath;
		this.sampleSize = sampleSize;
		this.reader = reader;
		this.indexer = indexer;
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		// load data from file
		LOGGER.info("Reading reviews from file. Please be patient...");
		List<Review> reviewList = this.reader.deserializeData(this.filePath);
		LOGGER.info("Data reading finished successfully. Total Reviews: {}", reviewList.size());

		// random sample
		LOGGER.info("Selecting a random sample of {} reviews.", this.sampleSize);
		Collections.shuffle(reviewList);    // might pass a custom source of randomness for better shuffling if needed
		// huge operation above, but as it is one timer, it should fine
		final Review[] reviews = new Review[this.sampleSize];
		reviewList.subList(0, this.sampleSize).toArray(reviews);
		LOGGER.debug("{} reviews sampled: {}", reviews.length, reviews);

		// create indexes
		LOGGER.info("Creating inverted indexes for the selected sample reviews. Please be patient...");
		this.indexer.setReviews(reviews);
		this.indexer.createIndexes();
		LOGGER.info("Indexing finished successfully. You can start your search now!");
	}
}
