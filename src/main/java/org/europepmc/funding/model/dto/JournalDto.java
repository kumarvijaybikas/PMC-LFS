package org.europepmc.funding.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JournalDto {

    private String title;
    private String volume;
    private String issue;
    private String dateOfPublication;
    private Integer pubYear;
    private String medlineAbbreviation;
    private String issn;
    private String essn;

    public JournalDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(String dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }

    public Integer getPubYear() {
        return pubYear;
    }

    public void setPubYear(Integer pubYear) {
        this.pubYear = pubYear;
    }

    public String getMedlineAbbreviation() {
        return medlineAbbreviation;
    }

    public void setMedlineAbbreviation(String medlineAbbreviation) {
        this.medlineAbbreviation = medlineAbbreviation;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getEssn() {
        return essn;
    }

    public void setEssn(String essn) {
        this.essn = essn;
    }
}
