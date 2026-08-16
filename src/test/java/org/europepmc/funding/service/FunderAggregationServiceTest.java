package org.europepmc.funding.service;

import org.europepmc.funding.model.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FunderAggregationServiceTest {

    private FunderAggregationService aggregationService;

    @BeforeEach
    void setUp() {
        aggregationService = new FunderAggregationService();
    }

    @Test
    void aggregate_shouldComputeAccurateFunderRankingsAndReverseLinks() {
        // Pub 1 has Wellcome Trust (084323) and MRC (MRC-100)
        PublicationDto pub1 = new PublicationDto();
        pub1.setId("MED:1001");

        FundingAssociationDto f1 = new FundingAssociationDto();
        f1.setGrantId("084323");
        f1.setStatus(FundingStatus.RESOLVED);
        GrantDetailsDto gd1 = new GrantDetailsDto();
        gd1.setGrantId("084323");
        gd1.setFunderName("Wellcome Trust");
        f1.setGrantDetails(gd1);

        FundingAssociationDto f2 = new FundingAssociationDto();
        f2.setGrantId("MRC-100");
        f2.setStatus(FundingStatus.RESOLVED);
        GrantDetailsDto gd2 = new GrantDetailsDto();
        gd2.setGrantId("MRC-100");
        gd2.setFunderName("Medical Research Council");
        f2.setGrantDetails(gd2);

        pub1.setFunding(List.of(f1, f2));

        // Pub 2 has Wellcome Trust (084323) as well
        PublicationDto pub2 = new PublicationDto();
        pub2.setId("MED:1002");

        FundingAssociationDto f3 = new FundingAssociationDto();
        f3.setGrantId("084323");
        f3.setStatus(FundingStatus.RESOLVED);
        f3.setGrantDetails(gd1);

        pub2.setFunding(List.of(f3));

        // Pub 3 has no grants
        PublicationDto pub3 = new PublicationDto();
        pub3.setId("MED:1003");

        FunderAggregationService.AggregationResult result = aggregationService.aggregate(List.of(pub1, pub2, pub3));

        SearchSummaryDto summary = result.summary();
        assertThat(summary.getTotalPublicationsReportedWithGrants()).isEqualTo(2);
        assertThat(summary.getTotalPublicationsWithoutGrants()).isEqualTo(1);
        assertThat(summary.getTotalGrantsReported()).isEqualTo(3);
        assertThat(summary.getTotalGrantsResolved()).isEqualTo(3);
        assertThat(summary.getTotalGrantsUnresolved()).isEqualTo(0);

        // Funder rankings: Wellcome Trust should be #1 (2 pubs, 1 grant), MRC #2 (1 pub, 1 grant)
        assertThat(summary.getFunderFrequency()).hasSize(2);
        assertThat(summary.getFunderFrequency().get(0).getFunderName()).isEqualTo("Wellcome Trust");
        assertThat(summary.getFunderFrequency().get(0).getPublicationCount()).isEqualTo(2);
        assertThat(summary.getFunderFrequency().get(0).getGrantCount()).isEqualTo(1);

        assertThat(summary.getFunderFrequency().get(1).getFunderName()).isEqualTo("Medical Research Council");
        assertThat(summary.getFunderFrequency().get(1).getPublicationCount()).isEqualTo(1);
        assertThat(summary.getFunderFrequency().get(1).getGrantCount()).isEqualTo(1);

        // Traceability of enrichedGrants
        List<EnrichedGrantSummaryDto> enrichedGrants = result.enrichedGrants();
        assertThat(enrichedGrants).hasSize(2);

        EnrichedGrantSummaryDto wellcomeGrant = enrichedGrants.stream()
                .filter(g -> "084323".equals(g.getGrantId()))
                .findFirst()
                .orElseThrow();
        assertThat(wellcomeGrant.getAssociatedPublicationIds()).containsExactlyInAnyOrder("MED:1001", "MED:1002");
    }
}
