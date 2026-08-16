package org.europepmc.funding.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FundingAssociationDto {

    private String grantId;
    private String reportedAgency;
    private String acronym;
    private String orderIn;
    private FundingStatus status;
    private String message;
    private GrantDetailsDto grantDetails;

    public FundingAssociationDto() {
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

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getOrderIn() {
        return orderIn;
    }

    public void setOrderIn(String orderIn) {
        this.orderIn = orderIn;
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
}
