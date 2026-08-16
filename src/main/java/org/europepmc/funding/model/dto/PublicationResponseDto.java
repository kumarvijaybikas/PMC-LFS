package org.europepmc.funding.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicationResponseDto {

    private String apiVersion;
    private String query;
    private int requestedLimit;
    private int returnedCount;
    private int totalAvailableHits;
    private String nextCursorMark;
    private boolean hasMore;
    private Instant timestamp = Instant.now();
    private SearchSummaryDto summary;
    private List<PublicationDto> publications = new ArrayList<>();
    private List<EnrichedGrantSummaryDto> enrichedGrants = new ArrayList<>();

    public PublicationResponseDto() {
    }

    public String getNextCursorMark() {
        return nextCursorMark;
    }

    public void setNextCursorMark(String nextCursorMark) {
        this.nextCursorMark = nextCursorMark;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getRequestedLimit() {
        return requestedLimit;
    }

    public void setRequestedLimit(int requestedLimit) {
        this.requestedLimit = requestedLimit;
    }

    public int getReturnedCount() {
        return returnedCount;
    }

    public void setReturnedCount(int returnedCount) {
        this.returnedCount = returnedCount;
    }

    public int getTotalAvailableHits() {
        return totalAvailableHits;
    }

    public void setTotalAvailableHits(int totalAvailableHits) {
        this.totalAvailableHits = totalAvailableHits;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public SearchSummaryDto getSummary() {
        return summary;
    }

    public void setSummary(SearchSummaryDto summary) {
        this.summary = summary;
    }

    public List<PublicationDto> getPublications() {
        return publications;
    }

    public void setPublications(List<PublicationDto> publications) {
        this.publications = publications;
    }

    public List<EnrichedGrantSummaryDto> getEnrichedGrants() {
        return enrichedGrants;
    }

    public void setEnrichedGrants(List<EnrichedGrantSummaryDto> enrichedGrants) {
        this.enrichedGrants = enrichedGrants;
    }
}
