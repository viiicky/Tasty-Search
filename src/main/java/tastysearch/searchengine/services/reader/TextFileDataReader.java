package tastysearch.searchengine.services.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tastysearch.searchengine.models.Review;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * File reader for text files.
 */
@Service
@Qualifier("textFileDataReader")
public class TextFileDataReader implements FileDataReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileDataReader.class);

    /**
     * Reads file using {@link BufferedReader} and deserializes the data to a {@link List} of {@link Review}s
     *
     * @param filePath path of file to be read
     * @return list of reviews deserialized from file
     */
    @Override
    public List<Review> deserializeData(String filePath) {
        List<Review> reviewList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filePath))) {
            String line = reader.readLine();
            while (line != null) {
                Review review = new Review();
                review.setProductId(line.split(":\\s")[1]);
                review.setUserId(reader.readLine().split(":\\s")[1]);
                review.setProfileName(reader.readLine().split(":\\s")[1]);
                String helpfulness = reader.readLine();
                if (!helpfulness.contains(": ")) {    // as data file is malformed
                    helpfulness = reader.readLine();
                }
                review.setHelpfulness(helpfulness.split(":\\s")[1]);
                review.setScore(Float.parseFloat(reader.readLine().split(":\\s")[1]));
                review.setTime(Long.parseLong(reader.readLine().split(":\\s")[1]));
                review.setSummary(reader.readLine().split(":\\s")[1]);
                review.setText(reader.readLine().split(":\\s")[1]);
                reader.readLine();
                line = reader.readLine();

                reviewList.add(review);
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading the file.", e);
        }

        return reviewList;
    }
}
