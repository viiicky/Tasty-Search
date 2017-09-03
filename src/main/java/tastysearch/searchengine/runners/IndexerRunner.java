package tastysearch.searchengine.runners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tastysearch.searchengine.models.Review;
import tastysearch.searchengine.services.indexer.Indexer;
import tastysearch.searchengine.services.reader.FileDataReader;

import java.util.Collections;
import java.util.List;

/**
 * This runner class provides the methods to be executed at application startup.
 */
@Component
public class IndexerRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexerRunner.class);
    private final int sampleSize;
    private final FileDataReader reader;

    private final Indexer indexer;

    @Autowired
    public IndexerRunner(
            @Value("${sample-size}") final int sampleSize,
            @Qualifier("textFileDataReader") final FileDataReader reader,
            @Qualifier("sequentialIndexer") final Indexer indexer) {

        this.sampleSize = sampleSize;
        this.reader = reader;
        this.indexer = indexer;
    }

    /*
    This method performs the following tasks:
     - loads data using {@link FileDataReader#deserializeData(String)}
     - randomly samples them
     - requests {@link Indexer#createIndexes()} to create indexes
     */
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (!applicationArguments.containsOption("dataset")) {
            throw new IllegalArgumentException("Please provide the file path using the argument 'dataset'");
        }

        List<String> filePath = applicationArguments.getOptionValues("dataset");
        if (filePath.size() != 1) {
            throw new IllegalArgumentException("Please provide exactly one value for the argument 'dataset'");
        }

        // load data from file
        LOGGER.info("Reading review data from file. Please be patient...");
        List<Review> reviewList = this.reader.deserializeData(filePath.get(0));
        LOGGER.info("Data reading finished successfully. Total Reviews: {}", reviewList.size());

        if (this.sampleSize > reviewList.size()) {
            throw new IllegalArgumentException("Sample size cannot be greater than population size.");
        }

        // random sample
        LOGGER.info("Selecting a random sample of {} reviews.", this.sampleSize);
        Collections.shuffle(reviewList);    // might pass a custom source of randomness for better shuffling if needed
        // huge operation above, but as it is one timer, it should be fine
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
