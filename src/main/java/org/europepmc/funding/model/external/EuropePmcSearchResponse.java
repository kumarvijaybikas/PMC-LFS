package org.europepmc.funding.model.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EuropePmcSearchResponse {

    private String version;
    private int hitCount;
    private String nextCursorMark;
    private String nextPageUrl;
    private ResultList resultList;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public String getNextCursorMark() {
        return nextCursorMark;
    }

    public void setNextCursorMark(String nextCursorMark) {
        this.nextCursorMark = nextCursorMark;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public ResultList getResultList() {
        return resultList;
    }

    public void setResultList(ResultList resultList) {
        this.resultList = resultList;
    }

    public List<ArticleResult> getResults() {
        return (resultList != null && resultList.getResult() != null)
                ? resultList.getResult()
                : Collections.emptyList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResultList {
        private List<ArticleResult> result;

        public List<ArticleResult> getResult() {
            return result;
        }

        public void setResult(List<ArticleResult> result) {
            this.result = result;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArticleResult {
        private String id;
        private String source;
        private String pmid;
        private String pmcid;
        private String doi;
        private String title;
        private String authorString;
        private AuthorList authorList;
        private JournalInfo journalInfo;
        private String pubYear;
        private String abstractText;
        private Integer citedByCount;
        private GrantsList grantsList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getPmid() {
            return pmid;
        }

        public void setPmid(String pmid) {
            this.pmid = pmid;
        }

        public String getPmcid() {
            return pmcid;
        }

        public void setPmcid(String pmcid) {
            this.pmcid = pmcid;
        }

        public String getDoi() {
            return doi;
        }

        public void setDoi(String doi) {
            this.doi = doi;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthorString() {
            return authorString;
        }

        public void setAuthorString(String authorString) {
            this.authorString = authorString;
        }

        public AuthorList getAuthorList() {
            return authorList;
        }

        public void setAuthorList(AuthorList authorList) {
            this.authorList = authorList;
        }

        public JournalInfo getJournalInfo() {
            return journalInfo;
        }

        public void setJournalInfo(JournalInfo journalInfo) {
            this.journalInfo = journalInfo;
        }

        public String getPubYear() {
            return pubYear;
        }

        public void setPubYear(String pubYear) {
            this.pubYear = pubYear;
        }

        public String getAbstractText() {
            return abstractText;
        }

        public void setAbstractText(String abstractText) {
            this.abstractText = abstractText;
        }

        public Integer getCitedByCount() {
            return citedByCount;
        }

        public void setCitedByCount(Integer citedByCount) {
            this.citedByCount = citedByCount;
        }

        public GrantsList getGrantsList() {
            return grantsList;
        }

        public void setGrantsList(GrantsList grantsList) {
            this.grantsList = grantsList;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorList {
        private List<Author> author;

        public List<Author> getAuthor() {
            return author;
        }

        public void setAuthor(List<Author> author) {
            this.author = author;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        private String fullName;
        private String firstName;
        private String lastName;
        private String initials;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JournalInfo {
        private String volume;
        private String issue;
        private String dateOfPublication;
        private Integer monthOfPublication;
        private Integer yearOfPublication;
        private String printPublicationDate;
        private Journal journal;

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

        public Integer getMonthOfPublication() {
            return monthOfPublication;
        }

        public void setMonthOfPublication(Integer monthOfPublication) {
            this.monthOfPublication = monthOfPublication;
        }

        public Integer getYearOfPublication() {
            return yearOfPublication;
        }

        public void setYearOfPublication(Integer yearOfPublication) {
            this.yearOfPublication = yearOfPublication;
        }

        public String getPrintPublicationDate() {
            return printPublicationDate;
        }

        public void setPrintPublicationDate(String printPublicationDate) {
            this.printPublicationDate = printPublicationDate;
        }

        public Journal getJournal() {
            return journal;
        }

        public void setJournal(Journal journal) {
            this.journal = journal;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Journal {
        private String title;
        private String medlineAbbreviation;
        private String essn;
        private String issn;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMedlineAbbreviation() {
            return medlineAbbreviation;
        }

        public void setMedlineAbbreviation(String medlineAbbreviation) {
            this.medlineAbbreviation = medlineAbbreviation;
        }

        public String getEssn() {
            return essn;
        }

        public void setEssn(String essn) {
            this.essn = essn;
        }

        public String getIssn() {
            return issn;
        }

        public void setIssn(String issn) {
            this.issn = issn;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GrantsList {
        private List<GrantItem> grant;

        public List<GrantItem> getGrant() {
            return grant != null ? grant : Collections.emptyList();
        }

        public void setGrant(List<GrantItem> grant) {
            this.grant = grant;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GrantItem {
        private String grantId;
        private String agency;
        private String acronym;
        private String orderIn;

        public String getGrantId() {
            return grantId;
        }

        public void setGrantId(String grantId) {
            this.grantId = grantId;
        }

        public String getAgency() {
            return agency;
        }

        public void setAgency(String agency) {
            this.agency = agency;
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
    }
}
