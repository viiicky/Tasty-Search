package home.searchengine.services.reader;

import java.util.List;

import home.searchengine.models.Review;

public interface FileDataReader {
	List<Review> deserializeData(String filePath);
}
