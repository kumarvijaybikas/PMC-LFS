package org.europepmc.funding.service;

import org.europepmc.funding.config.EuropePmcProperties;
import org.europepmc.funding.exception.EuropePmcApiException;
import org.europepmc.funding.model.external.EuropePmcSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class EuropePmcClient {

    private static final Logger log = LoggerFactory.getLogger(EuropePmcClient.class);

    private final RestClient restClient;
    private final EuropePmcProperties properties;

    public EuropePmcClient(RestClient restClient, EuropePmcProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public SearchResult searchArticles(String query, int limit, int requestedPageSize) {
        return searchArticles(query, limit, requestedPageSize, null);
    }

    /**
     * Searches Europe PMC Articles API and handles pagination up to the requested limit.
     */
    public SearchResult searchArticles(String query, int limit, int requestedPageSize, String startingCursorMark) {
        int pageSize = Math.min(requestedPageSize > 0 ? requestedPageSize : properties.getDefaultPageSize(), 100);
        int remainingToFetch = limit;
        String cursorMark = (startingCursorMark != null && !startingCursorMark.trim().isEmpty())
                ? startingCursorMark.trim()
                : "*";
        int totalHits = 0;
        String lastNextCursor = null;
        boolean hasMore = false;
        List<EuropePmcSearchResponse.ArticleResult> allArticles = new ArrayList<>();

        int pageIndex = 1;
        while (remainingToFetch > 0 && cursorMark != null) {
            int currentBatchSize = Math.min(remainingToFetch, pageSize);

            URI uri = UriComponentsBuilder.fromUriString(properties.getArticlesApiUrl())
                    .queryParam("query", query)
                    .queryParam("resultType", "core")
                    .queryParam("cursorMark", cursorMark)
                    .queryParam("pageSize", currentBatchSize)
                    .queryParam("format", "json")
                    .build()
                    .toUri();

            log.info("  --> [Europe PMC Search Page #{}] Requesting {} articles (cursorMark='{}')",
                    pageIndex, currentBatchSize, cursorMark);

            EuropePmcSearchResponse response = restClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        String body = new String(resp.getBody().readAllBytes());
                        log.error("Europe PMC search error: status={}, body={}", resp.getStatusCode(), body);
                        throw new EuropePmcApiException("Europe PMC search request failed: " + resp.getStatusText(),
                                resp.getStatusCode().value());
                    })
                    .body(EuropePmcSearchResponse.class);

            if (response == null || response.getResults().isEmpty()) {
                if (response != null) {
                    totalHits = response.getHitCount();
                }
                log.info("  <-- [Europe PMC Search Page #{}] 0 results returned. Reached end of dataset.", pageIndex);
                break;
            }

            totalHits = response.getHitCount();
            List<EuropePmcSearchResponse.ArticleResult> pageResults = response.getResults();
            allArticles.addAll(pageResults);
            remainingToFetch -= pageResults.size();

            log.info("  <-- [Europe PMC Search Page #{}] Received {} articles (Progress: {}/{} fetched, nextCursorMark='{}')",
                    pageIndex, pageResults.size(), allArticles.size(), limit, response.getNextCursorMark());

            // Stop if there are no more results or nextCursorMark is identical / null
            String nextCursor = response.getNextCursorMark();
            lastNextCursor = nextCursor;
            if (nextCursor == null || nextCursor.equals(cursorMark) || pageResults.size() < currentBatchSize) {
                hasMore = false;
                break;
            }
            hasMore = true;
            cursorMark = nextCursor;
            pageIndex++;
        }

        boolean hasMoreResults = hasMore && (totalHits > allArticles.size());
        String nextCursorToReturn = hasMoreResults ? lastNextCursor : null;

        return new SearchResult(totalHits, allArticles, nextCursorToReturn, hasMoreResults);
    }

    public record SearchResult(int totalHits, List<EuropePmcSearchResponse.ArticleResult> articles,
                              String nextCursorMark, boolean hasMore) {
    }
}
