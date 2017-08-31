package home.searchengine.services.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import home.searchengine.models.Review;

@Service
@Qualifier("textFileDataReader")
public class TextFileDataReader implements FileDataReader {

	/**
	 * assumes data to be in specific structure in file
	 * TODO: Share on SO and ask if there is a faster way to do this
	 * @param filePath
	 * @return
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
				if (!helpfulness.contains(": ")) {	// as data file is malformed
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
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return reviewList;
	}
}
