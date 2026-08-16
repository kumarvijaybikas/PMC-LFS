package org.europepmc.funding.service;

import org.europepmc.funding.config.EuropePmcProperties;
import org.europepmc.funding.exception.InvalidSearchQueryException;
import org.europepmc.funding.model.dto.*;
import org.europepmc.funding.model.external.EuropePmcSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicationSearchService {

    private static final Logger log = LoggerFactory.getLogger(PublicationSearchService.class);

    private final EuropePmcClient europePmcClient;
    private final GrantEnrichmentService grantEnrichmentService;
    private final FunderAggregationService funderAggregationService;
    private final EuropePmcProperties properties;

    public PublicationSearchService(EuropePmcClient europePmcClient,
                                    GrantEnrichmentService grantEnrichmentService,
                                    FunderAggregationService funderAggregationService,
                                    EuropePmcProperties properties) {
        this.europePmcClient = europePmcClient;
        this.grantEnrichmentService = grantEnrichmentService;
        this.funderAggregationService = funderAggregationService;
        this.properties = properties;
    }

    /**
     * Executes literature search, enriches publication funding information, and generates aggregation statistics.
     */
    public PublicationResponseDto searchAndEnrichPublications(String query, Integer requestedLimit, Integer requestedPageSize, String cursorMark) {
        if (query == null || query.trim().isEmpty()) {
            throw new InvalidSearchQueryException("Search query must not be empty or blank.");
        }

        int limit = requestedLimit != null ? requestedLimit : properties.getDefaultPageSize();
        if (limit <= 0) {
            throw new InvalidSearchQueryException("Requested limit must be greater than 0.");
        }
        if (limit > properties.getMaxLimit()) {
            throw new InvalidSearchQueryException("Requested limit (" + limit + ") exceeds maximum allowed (" + properties.getMaxLimit() + ").");
        }

        int pageSize = requestedPageSize != null ? requestedPageSize : properties.getDefaultPageSize();
        if (pageSize <= 0) {
            pageSize = properties.getDefaultPageSize();
        }

        log.info("================================================================================");
        log.info("[START] Literature Funding Search | query='{}', limit={}, pageSize={}, cursorMark='{}'",
                query, limit, pageSize, cursorMark);

        // 1. Search publications from Europe PMC Articles REST API
        log.info("[STEP 1/3: SEARCH] Querying Europe PMC Articles REST API...");
        EuropePmcClient.SearchResult searchResult = europePmcClient.searchArticles(query.trim(), limit, pageSize, cursorMark);
        List<EuropePmcSearchResponse.ArticleResult> rawArticles = searchResult.articles();
        log.info("[STEP 1/3: SEARCH COMPLETE] Retrieved {} publication(s) (Total hits matching query: {})",
                rawArticles.size(), searchResult.totalHits());

        // 2. Enrich grant funding info for each publication
        log.info("[STEP 2/3: ENRICH] Resolving and enriching grant funding via Europe PMC Grants (Grist) API...");
        List<PublicationDto> publicationDtos = rawArticles.stream()
                .map(this::mapToPublicationDto)
                .collect(Collectors.toList());

        long resolvedCount = publicationDtos.stream()
                .flatMap(p -> p.getFunding().stream())
                .filter(f -> f.getStatus() == FundingStatus.RESOLVED)
                .count();
        long unresolvedCount = publicationDtos.stream()
                .flatMap(p -> p.getFunding().stream())
                .filter(f -> f.getStatus() == FundingStatus.UNRESOLVED)
                .count();

        log.info("[STEP 2/3: ENRICH COMPLETE] Processed all grants | {} resolved, {} unresolved/reported",
                resolvedCount, unresolvedCount);

        // 3. Aggregate funder frequency and top-level summaries
        log.info("[STEP 3/3: AGGREGATE] Aggregating funder frequency rankings and establishing bidirectional traceability...");
        FunderAggregationService.AggregationResult aggregation = funderAggregationService.aggregate(publicationDtos);
        log.info("[STEP 3/3: AGGREGATE COMPLETE] Top {} funder(s) ranked | {} unique enriched grant entities cataloged",
                aggregation.summary().getFunderFrequency().size(),
                aggregation.enrichedGrants().size());

        PublicationResponseDto responseDto = new PublicationResponseDto();
        responseDto.setApiVersion(properties.getApiVersion());
        responseDto.setQuery(query.trim());
        responseDto.setRequestedLimit(limit);
        responseDto.setReturnedCount(publicationDtos.size());
        responseDto.setTotalAvailableHits(searchResult.totalHits());
        responseDto.setNextCursorMark(searchResult.nextCursorMark());
        responseDto.setHasMore(searchResult.hasMore());
        responseDto.setSummary(aggregation.summary());
        responseDto.setPublications(publicationDtos);
        responseDto.setEnrichedGrants(aggregation.enrichedGrants());

        log.info("[END] Completed literature funding pipeline for query='{}'", query);
        log.info("================================================================================");

        return responseDto;
    }

    public PublicationResponseDto searchAndEnrichPublications(String query, Integer requestedLimit, Integer requestedPageSize) {
        return searchAndEnrichPublications(query, requestedLimit, requestedPageSize, null);
    }

    private PublicationDto mapToPublicationDto(EuropePmcSearchResponse.ArticleResult article) {
        PublicationDto dto = new PublicationDto();
        // Construct unique publication identifier
        String identifier = article.getId();
        if (identifier == null || identifier.isBlank()) {
            identifier = article.getPmid() != null ? article.getPmid() : article.getDoi();
        }
        if (article.getSource() != null && !article.getSource().isBlank() && identifier != null && !identifier.contains(":")) {
            dto.setId(article.getSource() + ":" + identifier);
        } else {
            dto.setId(identifier);
        }

        dto.setSource(article.getSource());
        dto.setPmid(article.getPmid());
        dto.setPmcid(article.getPmcid());
        dto.setDoi(article.getDoi());
        dto.setTitle(article.getTitle());
        dto.setAuthorString(article.getAuthorString());
        dto.setPubYear(article.getPubYear());
        dto.setAbstractText(article.getAbstractText());
        dto.setCitedByCount(article.getCitedByCount() != null ? article.getCitedByCount() : 0);

        // Authors list
        if (article.getAuthorList() != null && article.getAuthorList().getAuthor() != null) {
            List<String> authors = article.getAuthorList().getAuthor().stream()
                    .map(a -> a.getFullName() != null ? a.getFullName() : (a.getLastName() + " " + a.getInitials()).trim())
                    .toList();
            dto.setAuthors(authors);
        } else if (article.getAuthorString() != null) {
            dto.setAuthors(List.of(article.getAuthorString().split(",\\s*")));
        }

        // Journal
        if (article.getJournalInfo() != null) {
            EuropePmcSearchResponse.JournalInfo jInfo = article.getJournalInfo();
            JournalDto journalDto = new JournalDto();
            if (jInfo.getJournal() != null) {
                journalDto.setTitle(jInfo.getJournal().getTitle());
                journalDto.setMedlineAbbreviation(jInfo.getJournal().getMedlineAbbreviation());
                journalDto.setIssn(jInfo.getJournal().getIssn());
                journalDto.setEssn(jInfo.getJournal().getEssn());
            }
            journalDto.setVolume(jInfo.getVolume());
            journalDto.setIssue(jInfo.getIssue());
            journalDto.setDateOfPublication(jInfo.getDateOfPublication());
            journalDto.setPubYear(jInfo.getYearOfPublication());
            dto.setJournal(journalDto);
        }

        // Grants & Funding
        if (article.getGrantsList() != null && article.getGrantsList().getGrant() != null) {
            List<FundingAssociationDto> fundings = new ArrayList<>();
            for (EuropePmcSearchResponse.GrantItem grantItem : article.getGrantsList().getGrant()) {
                FundingAssociationDto fundingDto = grantEnrichmentService.enrichGrant(grantItem);
                fundings.add(fundingDto);
            }
            dto.setFunding(fundings);
        }

        return dto;
    }
}
