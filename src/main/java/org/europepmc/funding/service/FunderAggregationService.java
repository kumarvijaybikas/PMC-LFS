package org.europepmc.funding.service;

import org.europepmc.funding.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FunderAggregationService {

    /**
     * Builds summary metrics, funder occurrence frequency, and bidirectional grant-publication associations.
     */
    public AggregationResult aggregate(List<PublicationDto> publications) {
        int pubsWithGrants = 0;
        int pubsWithoutGrants = 0;
        int totalGrantsReported = 0;
        int totalGrantsResolved = 0;
        int totalGrantsUnresolved = 0;
        int totalGrantsAmbiguous = 0;

        // Funder tracking: FunderName -> Set of Publication IDs & Set of Grant IDs
        Map<String, Set<String>> funderPublications = new HashMap<>();
        Map<String, Set<String>> funderGrants = new HashMap<>();

        // Grant ID -> EnrichedGrantSummaryDto
        Map<String, EnrichedGrantSummaryDto> grantSummaryMap = new LinkedHashMap<>();

        for (PublicationDto pub : publications) {
            List<FundingAssociationDto> fundings = pub.getFunding();
            if (fundings == null || fundings.isEmpty()) {
                pubsWithoutGrants++;
                continue;
            }

            pubsWithGrants++;
            totalGrantsReported += fundings.size();

            for (FundingAssociationDto funding : fundings) {
                if (funding.getStatus() == FundingStatus.RESOLVED) {
                    totalGrantsResolved++;
                } else if (funding.getStatus() == FundingStatus.AMBIGUOUS) {
                    totalGrantsAmbiguous++;
                } else {
                    totalGrantsUnresolved++;
                }

                // Determine effective funder name: resolved funder name, fallback to reported agency, or "Unknown Funder"
                String funderName = null;
                if (funding.getGrantDetails() != null && funding.getGrantDetails().getFunderName() != null) {
                    funderName = funding.getGrantDetails().getFunderName();
                } else if (funding.getReportedAgency() != null && !funding.getReportedAgency().isBlank()) {
                    funderName = funding.getReportedAgency();
                }

                if (funderName != null && !funderName.isBlank()) {
                    String normFunder = funderName.trim();
                    funderPublications.computeIfAbsent(normFunder, k -> new HashSet<>()).add(pub.getId());
                    if (funding.getGrantId() != null) {
                        funderGrants.computeIfAbsent(normFunder, k -> new HashSet<>()).add(funding.getGrantId());
                    }
                }

                // Aggregate unique grants for top-level enrichedGrants registry with publication IDs
                String grantKey = funding.getGrantId() != null && !funding.getGrantId().isBlank()
                        ? funding.getGrantId().trim()
                        : "UNRESOLVED-" + UUID.randomUUID();

                EnrichedGrantSummaryDto grantSummary = grantSummaryMap.computeIfAbsent(grantKey, k -> {
                    EnrichedGrantSummaryDto summaryDto = new EnrichedGrantSummaryDto();
                    summaryDto.setGrantId(funding.getGrantId());
                    summaryDto.setReportedAgency(funding.getReportedAgency());
                    summaryDto.setStatus(funding.getStatus());
                    summaryDto.setMessage(funding.getMessage());
                    summaryDto.setGrantDetails(funding.getGrantDetails());
                    return summaryDto;
                });

                if (pub.getId() != null && !grantSummary.getAssociatedPublicationIds().contains(pub.getId())) {
                    grantSummary.getAssociatedPublicationIds().add(pub.getId());
                }
            }
        }

        // Build sorted funder frequencies (highest publication count first, then grant count)
        List<FunderFrequencyDto> funderFrequencies = funderPublications.entrySet().stream()
                .map(entry -> {
                    String funder = entry.getKey();
                    long pubCount = entry.getValue().size();
                    long grantCount = funderGrants.getOrDefault(funder, Collections.emptySet()).size();
                    return new FunderFrequencyDto(funder, pubCount, grantCount);
                })
                .sorted(Comparator.comparingLong(FunderFrequencyDto::getPublicationCount)
                        .thenComparingLong(FunderFrequencyDto::getGrantCount)
                        .reversed()
                        .thenComparing(FunderFrequencyDto::getFunderName))
                .collect(Collectors.toList());

        SearchSummaryDto summary = new SearchSummaryDto();
        summary.setTotalPublicationsReportedWithGrants(pubsWithGrants);
        summary.setTotalPublicationsWithoutGrants(pubsWithoutGrants);
        summary.setTotalGrantsReported(totalGrantsReported);
        summary.setTotalGrantsResolved(totalGrantsResolved);
        summary.setTotalGrantsUnresolved(totalGrantsUnresolved);
        summary.setTotalGrantsAmbiguous(totalGrantsAmbiguous);
        summary.setFunderFrequency(funderFrequencies);

        List<EnrichedGrantSummaryDto> enrichedGrants = new ArrayList<>(grantSummaryMap.values());

        return new AggregationResult(summary, enrichedGrants);
    }

    public record AggregationResult(SearchSummaryDto summary, List<EnrichedGrantSummaryDto> enrichedGrants) {
    }
}
