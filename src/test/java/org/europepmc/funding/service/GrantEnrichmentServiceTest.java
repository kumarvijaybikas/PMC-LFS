package org.europepmc.funding.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.europepmc.funding.config.RestClientConfig;
import org.europepmc.funding.model.dto.FundingAssociationDto;
import org.europepmc.funding.model.dto.FundingStatus;
import org.europepmc.funding.model.external.EuropePmcSearchResponse;
import org.europepmc.funding.model.external.GristApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GrantEnrichmentServiceTest {

    @Mock
    private GrantsApiClient grantsApiClient;

    private GrantEnrichmentService enrichmentService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        enrichmentService = new GrantEnrichmentService(grantsApiClient);
        objectMapper = new RestClientConfig().objectMapper();
    }

    @Test
    void enrichGrant_shouldResolveGrant_whenSingleRecordFound() throws Exception {
        String grantJson = Files.readString(Path.of("src/test/resources/fixtures/grist-grant-084323.json"));
        GristApiResponse gristResponse = objectMapper.readValue(grantJson, GristApiResponse.class);

        when(grantsApiClient.fetchGrantById("084323")).thenReturn(Optional.of(gristResponse));

        EuropePmcSearchResponse.GrantItem item = new EuropePmcSearchResponse.GrantItem();
        item.setGrantId("084323");
        item.setAgency("Wellcome Trust");

        FundingAssociationDto result = enrichmentService.enrichGrant(item);

        assertThat(result.getStatus()).isEqualTo(FundingStatus.RESOLVED);
        assertThat(result.getGrantDetails()).isNotNull();
        assertThat(result.getGrantDetails().getGrantId()).isEqualTo("084323");
        assertThat(result.getGrantDetails().getFunderName()).isEqualTo("Wellcome Trust");
        assertThat(result.getGrantDetails().getPrincipalInvestigator()).isEqualTo("Prof Robert Wilkinson");
        assertThat(result.getGrantDetails().getInstitution()).isEqualTo("University of Cape Town");
        assertThat(result.getGrantDetails().getAmount()).isEqualTo(3272110.0);
        assertThat(result.getGrantDetails().getCurrency()).isEqualTo("GBP");
    }

    @Test
    void enrichGrant_shouldMarkUnresolved_whenZeroHitsReturned() throws Exception {
        String emptyJson = Files.readString(Path.of("src/test/resources/fixtures/grist-grant-empty.json"));
        GristApiResponse gristResponse = objectMapper.readValue(emptyJson, GristApiResponse.class);

        when(grantsApiClient.fetchGrantById("UNKNOWN-999")).thenReturn(Optional.of(gristResponse));

        EuropePmcSearchResponse.GrantItem item = new EuropePmcSearchResponse.GrantItem();
        item.setGrantId("UNKNOWN-999");
        item.setAgency("Unknown Agency");

        FundingAssociationDto result = enrichmentService.enrichGrant(item);

        assertThat(result.getStatus()).isEqualTo(FundingStatus.UNRESOLVED);
        assertThat(result.getGrantDetails()).isNull();
        assertThat(result.getMessage()).contains("No matching record found");
    }

    @Test
    void enrichGrant_shouldDisambiguate_whenMultipleHitsMatchReportedAgency() throws Exception {
        String ambiguousJson = Files.readString(Path.of("src/test/resources/fixtures/grist-grant-ambiguous.json"));
        GristApiResponse gristResponse = objectMapper.readValue(ambiguousJson, GristApiResponse.class);

        when(grantsApiClient.fetchGrantById("AMBIG-123")).thenReturn(Optional.of(gristResponse));

        EuropePmcSearchResponse.GrantItem item = new EuropePmcSearchResponse.GrantItem();
        item.setGrantId("AMBIG-123");
        item.setAgency("Agency Beta");

        FundingAssociationDto result = enrichmentService.enrichGrant(item);

        assertThat(result.getStatus()).isEqualTo(FundingStatus.RESOLVED);
        assertThat(result.getGrantDetails().getTitle()).isEqualTo("Project Beta");
    }

    @Test
    void enrichGrant_shouldMarkAmbiguous_whenMultipleHitsCannotBeDisambiguated() throws Exception {
        String ambiguousJson = Files.readString(Path.of("src/test/resources/fixtures/grist-grant-ambiguous.json"));
        GristApiResponse gristResponse = objectMapper.readValue(ambiguousJson, GristApiResponse.class);

        when(grantsApiClient.fetchGrantById("AMBIG-123")).thenReturn(Optional.of(gristResponse));

        EuropePmcSearchResponse.GrantItem item = new EuropePmcSearchResponse.GrantItem();
        item.setGrantId("AMBIG-123");
        item.setAgency("Unrelated Agency");

        FundingAssociationDto result = enrichmentService.enrichGrant(item);

        assertThat(result.getStatus()).isEqualTo(FundingStatus.AMBIGUOUS);
        assertThat(result.getGrantDetails()).isNull();
        assertThat(result.getMessage()).contains("Multiple (2) distinct records found");
    }

    @Test
    void enrichGrant_shouldMarkUnresolved_whenGrantIdIsMissing() {
        EuropePmcSearchResponse.GrantItem item = new EuropePmcSearchResponse.GrantItem();
        item.setAgency("Some Agency");

        FundingAssociationDto result = enrichmentService.enrichGrant(item);

        assertThat(result.getStatus()).isEqualTo(FundingStatus.UNRESOLVED);
        assertThat(result.getMessage()).contains("No grant identifier provided");
    }
}
