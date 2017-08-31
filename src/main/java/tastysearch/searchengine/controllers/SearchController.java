package tastysearch.searchengine.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tastysearch.searchengine.models.Review;
import tastysearch.searchengine.services.searcher.SearchService;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Controller to handle http requests by an external client.
 */
@RestController
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
    private final SearchService searchService;

    @Autowired
    public SearchController(@Qualifier("intermediateHashTableSearcher") SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Serves the GET request made to '/reviews' and delegates the call to searchService's searchReviews() to process
     * the request.
     *
     * @param params search tokens to look for in {@link Review}
     * @return array of reviews returned by searchService's searchReviews()
     * @see SearchService#searchReviews
     */
    @RequestMapping(path = "/reviews", method = GET, produces = APPLICATION_JSON_VALUE)
    public Review[] searchReviews(@RequestParam Map<String, String> params) {
        LOGGER.debug("Query Params: {}", params);
        return this.searchService.searchReviews(params.values());
    }
}
