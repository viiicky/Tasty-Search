package home.searchengine.services.searcher;

import java.util.Collection;

import home.searchengine.models.Review;

public interface SearchService {
	Review[] searchReviews(Collection<String> inputTokens);
}
