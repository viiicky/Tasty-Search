package home.searchengine.controllers;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import home.searchengine.models.Review;
import home.searchengine.services.searcher.SearchService;

@RestController
public class SearchController {

	private final SearchService searchService;

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	public SearchController(@Qualifier("intermediateHashTableSearcher") SearchService searchService) {
		this.searchService = searchService;
	}

	@RequestMapping(path = "/reviews", method = GET, produces = APPLICATION_JSON_VALUE)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Review[] searchReviews(@RequestParam Map<String, String> params) {
		LOGGER.debug("Query Params: {}", params);
		return this.searchService.searchReviews(params.values());
	}
}
