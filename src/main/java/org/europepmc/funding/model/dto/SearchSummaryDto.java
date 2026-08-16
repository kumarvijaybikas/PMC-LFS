package org.europepmc.funding.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchSummaryDto {

    private int totalPublicationsReportedWithGrants;
    private int totalPublicationsWithoutGrants;
    private int totalGrantsReported;
    private int totalGrantsResolved;
    private int totalGrantsUnresolved;
    private int totalGrantsAmbiguous;
    private List<FunderFrequencyDto> funderFrequency = new ArrayList<>();

    public SearchSummaryDto() {
    }

    public int getTotalPublicationsReportedWithGrants() {
        return totalPublicationsReportedWithGrants;
    }

    public void setTotalPublicationsReportedWithGrants(int totalPublicationsReportedWithGrants) {
        this.totalPublicationsReportedWithGrants = totalPublicationsReportedWithGrants;
    }

    public int getTotalPublicationsWithoutGrants() {
        return totalPublicationsWithoutGrants;
    }

    public void setTotalPublicationsWithoutGrants(int totalPublicationsWithoutGrants) {
        this.totalPublicationsWithoutGrants = totalPublicationsWithoutGrants;
    }

    public int getTotalGrantsReported() {
        return totalGrantsReported;
    }

    public void setTotalGrantsReported(int totalGrantsReported) {
        this.totalGrantsReported = totalGrantsReported;
    }

    public int getTotalGrantsResolved() {
        return totalGrantsResolved;
    }

    public void setTotalGrantsResolved(int totalGrantsResolved) {
        this.totalGrantsResolved = totalGrantsResolved;
    }

    public int getTotalGrantsUnresolved() {
        return totalGrantsUnresolved;
    }

    public void setTotalGrantsUnresolved(int totalGrantsUnresolved) {
        this.totalGrantsUnresolved = totalGrantsUnresolved;
    }

    public int getTotalGrantsAmbiguous() {
        return totalGrantsAmbiguous;
    }

    public void setTotalGrantsAmbiguous(int totalGrantsAmbiguous) {
        this.totalGrantsAmbiguous = totalGrantsAmbiguous;
    }

    public List<FunderFrequencyDto> getFunderFrequency() {
        return funderFrequency;
    }

    public void setFunderFrequency(List<FunderFrequencyDto> funderFrequency) {
        this.funderFrequency = funderFrequency;
    }
}
