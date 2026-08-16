package org.europepmc.funding.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrichedGrantSummaryDto {

    private String grantId;
    private String reportedAgency;
    private FundingStatus status;
    private String message;
    private GrantDetailsDto grantDetails;
    private List<String> associatedPublicationIds = new ArrayList<>();

    public EnrichedGrantSummaryDto() {
    }

    public String getGrantId() {
        return grantId;
    }

    public void setGrantId(String grantId) {
        this.grantId = grantId;
    }

    public String getReportedAgency() {
        return reportedAgency;
    }

    public void setReportedAgency(String reportedAgency) {
        this.reportedAgency = reportedAgency;
    }

    public FundingStatus getStatus() {
        return status;
    }

    public void setStatus(FundingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public GrantDetailsDto getGrantDetails() {
        return grantDetails;
    }

    public void setGrantDetails(GrantDetailsDto grantDetails) {
        this.grantDetails = grantDetails;
    }

    public List<String> getAssociatedPublicationIds() {
        return associatedPublicationIds;
    }

    public void setAssociatedPublicationIds(List<String> associatedPublicationIds) {
        this.associatedPublicationIds = associatedPublicationIds;
    }
}
