package org.europepmc.funding.model.dto;

public class FunderFrequencyDto {

    private String funderName;
    private long publicationCount;
    private long grantCount;

    public FunderFrequencyDto() {
    }

    public FunderFrequencyDto(String funderName, long publicationCount, long grantCount) {
        this.funderName = funderName;
        this.publicationCount = publicationCount;
        this.grantCount = grantCount;
    }

    public String getFunderName() {
        return funderName;
    }

    public void setFunderName(String funderName) {
        this.funderName = funderName;
    }

    public long getPublicationCount() {
        return publicationCount;
    }

    public void setPublicationCount(long publicationCount) {
        this.publicationCount = publicationCount;
    }

    public long getGrantCount() {
        return grantCount;
    }

    public void setGrantCount(long grantCount) {
        this.grantCount = grantCount;
    }
}
