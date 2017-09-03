package tastysearch.searchengine.services.reader;

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

    /**
     * Reads file using {@link BufferedReader} and deserializes the data to a {@link List} of {@link Review}s
     *
     * @param filePath path of file to be read
     * @return list of reviews deserialized from file
     */
    @Override
    public List<Review> deserializeData(String filePath) throws IOException {
        List<Review> reviewList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(filePath))) {
            String line = reader.readLine();
            while (line != null) {
                Review review = new Review();
                review.setProductId(line.substring(line.indexOf(" ")+1));
                line = reader.readLine();
                review.setUserId(line.substring(line.indexOf(" ")+1));
                line = reader.readLine();
                review.setProfileName(line.substring(line.indexOf(" ")+1));
                line = reader.readLine();
                if (!line.contains(": ")) {    // as data file is malformed for helpfulness field
                    line = reader.readLine();
                }
                review.setHelpfulness(line.substring(line.indexOf(" ")+1));
                line = reader.readLine();
                review.setScore(Float.parseFloat(line.substring(line.indexOf(" ")+1)));
                line = reader.readLine();
                review.setTime(Long.parseLong(line.substring(line.indexOf(" ")+1)));
                line = reader.readLine();
                review.setSummary(line.substring(line.indexOf(" ")+1));
                line = reader.readLine();
                review.setText(line.substring(line.indexOf(" ")+1));
                reader.readLine();
                line = reader.readLine();

                reviewList.add(review);
            }
        }

        return reviewList;
    }
}
