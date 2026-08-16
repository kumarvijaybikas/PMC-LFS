package org.europepmc.funding.model.dto;

public enum FundingStatus {
    /**
     * Grant is reported in publication metadata and was successfully resolved in Grants API.
     */
    RESOLVED,

    /**
     * Grant is reported in publication metadata but could not be found in Grants API.
     */
    UNRESOLVED,

    /**
     * Multiple ambiguous records were returned by Grants API for the given identifier.
     */
    AMBIGUOUS,

    /**
     * Grant was reported in publication metadata without further enrichment attempted.
     */
    REPORTED
}
