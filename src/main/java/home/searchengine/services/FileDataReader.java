package home.searchengine.services;

import java.util.List;

import home.searchengine.models.Review;

public interface FileDataReader {
	List<Review> deserializeData(String filePath);
}
