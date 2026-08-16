package org.europepmc.funding.service;

import org.europepmc.funding.model.dto.FundingAssociationDto;
import org.europepmc.funding.model.dto.FundingStatus;
import org.europepmc.funding.model.dto.GrantDetailsDto;
import org.europepmc.funding.model.external.EuropePmcSearchResponse;
import org.europepmc.funding.model.external.GristApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrantEnrichmentService {

    private static final Logger log = LoggerFactory.getLogger(GrantEnrichmentService.class);

    private final GrantsApiClient grantsApiClient;

    public GrantEnrichmentService(GrantsApiClient grantsApiClient) {
        this.grantsApiClient = grantsApiClient;
    }

    /**
     * Enriches a single grant item reported in publication metadata.
     */
    public FundingAssociationDto enrichGrant(EuropePmcSearchResponse.GrantItem grantItem) {
        FundingAssociationDto dto = new FundingAssociationDto();
        dto.setGrantId(grantItem.getGrantId());
        dto.setReportedAgency(grantItem.getAgency());
        dto.setAcronym(grantItem.getAcronym());
        dto.setOrderIn(grantItem.getOrderIn());

        if (grantItem.getGrantId() == null || grantItem.getGrantId().trim().isEmpty()) {
            dto.setStatus(FundingStatus.UNRESOLVED);
            dto.setMessage("No grant identifier provided in publication metadata");
            return dto;
        }

        String grantId = grantItem.getGrantId().trim();
        Optional<GristApiResponse> gristResponseOpt = grantsApiClient.fetchGrantById(grantId);

        if (gristResponseOpt.isEmpty()) {
            dto.setStatus(FundingStatus.UNRESOLVED);
            dto.setMessage("Grants API request failed or was unavailable for grant ID: " + grantId);
            return dto;
        }

        GristApiResponse gristResponse = gristResponseOpt.get();
        List<GristApiResponse.GristRecord> records = gristResponse.getRecords();

        if (records.isEmpty() || gristResponse.getHitCountAsInt() == 0) {
            dto.setStatus(FundingStatus.UNRESOLVED);
            dto.setMessage("No matching record found in Grants API for grant ID: " + grantId);
            return dto;
        }

        if (records.size() == 1) {
            GristApiResponse.GristRecord record = records.get(0);
            dto.setStatus(FundingStatus.RESOLVED);
            dto.setGrantDetails(mapToGrantDetails(record, grantId));
            return dto;
        }

        // Multiple records returned - attempt disambiguation by funder / agency name
        if (grantItem.getAgency() != null && !grantItem.getAgency().isBlank()) {
            String reportedAgencyNorm = normalizeAgencyName(grantItem.getAgency());
            List<GristApiResponse.GristRecord> matching = records.stream()
                    .filter(r -> r.getGrant() != null && r.getGrant().getFunder() != null
                            && r.getGrant().getFunder().getName() != null
                            && normalizeAgencyName(r.getGrant().getFunder().getName()).contains(reportedAgencyNorm))
                    .toList();

            if (matching.size() == 1) {
                dto.setStatus(FundingStatus.RESOLVED);
                dto.setGrantDetails(mapToGrantDetails(matching.get(0), grantId));
                dto.setMessage("Disambiguated via matching funder agency: " + grantItem.getAgency());
                return dto;
            }
        }

        // Multiple unresolved ambiguous records
        dto.setStatus(FundingStatus.AMBIGUOUS);
        dto.setMessage("Multiple (" + records.size() + ") distinct records found in Grants API for grant ID: " + grantId);
        return dto;
    }

    private GrantDetailsDto mapToGrantDetails(GristApiResponse.GristRecord record, String defaultGrantId) {
        GrantDetailsDto details = new GrantDetailsDto();
        if (record.getGrant() != null) {
            GristApiResponse.GristGrant grant = record.getGrant();
            details.setGrantId(grant.getId() != null ? grant.getId() : defaultGrantId);
            details.setDoi(grant.getDoi());
            details.setTitle(grant.getTitle());
            details.setGrantType(grant.getType());
            details.setStartDate(grant.getStartDate());
            details.setEndDate(grant.getEndDate());

            if (grant.getFunder() != null) {
                details.setFunderName(grant.getFunder().getName());
                details.setFundRefId(grant.getFunder().getFundRefId());
            }

            if (grant.getAmount() != null) {
                details.setAmount(grant.getAmount().getValue());
                details.setCurrency(grant.getAmount().getCurrency());
            }
        } else {
            details.setGrantId(defaultGrantId);
        }

        if (record.getPerson() != null) {
            details.setPrincipalInvestigator(record.getPerson().getFormattedName());
        }

        if (record.getInstitution() != null) {
            details.setInstitution(record.getInstitution().getName());
            details.setRorId(record.getInstitution().getRorId());
        }

        return details;
    }

    private String normalizeAgencyName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}
