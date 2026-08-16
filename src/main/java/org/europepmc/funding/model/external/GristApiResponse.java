package org.europepmc.funding.model.external;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GristApiResponse {

    @JsonProperty("HitCount")
    private String hitCount;

    @JsonProperty("RecordList")
    private RecordListWrapper recordList;

    public String getHitCount() {
        return hitCount;
    }

    public void setHitCount(String hitCount) {
        this.hitCount = hitCount;
    }

    public int getHitCountAsInt() {
        if (hitCount == null || hitCount.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(hitCount.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public RecordListWrapper getRecordList() {
        return recordList;
    }

    public void setRecordList(RecordListWrapper recordList) {
        this.recordList = recordList;
    }

    public List<GristRecord> getRecords() {
        if (recordList != null && recordList.getRecord() != null) {
            return recordList.getRecord();
        }
        return Collections.emptyList();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecordListWrapper {

        @JsonProperty("Record")
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<GristRecord> record;

        public List<GristRecord> getRecord() {
            return record != null ? record : Collections.emptyList();
        }

        public void setRecord(List<GristRecord> record) {
            this.record = record;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GristRecord {

        @JsonProperty("Person")
        private GristPerson person;

        @JsonProperty("Grant")
        private GristGrant grant;

        @JsonProperty("Institution")
        private GristInstitution institution;

        public GristPerson getPerson() {
            return person;
        }

        public void setPerson(GristPerson person) {
            this.person = person;
        }

        public GristGrant getGrant() {
            return grant;
        }

        public void setGrant(GristGrant grant) {
            this.grant = grant;
        }

        public GristInstitution getInstitution() {
            return institution;
        }

        public void setInstitution(GristInstitution institution) {
            this.institution = institution;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GristPerson {
        @JsonProperty("FamilyName")
        private String familyName;

        @JsonProperty("GivenName")
        private String givenName;

        @JsonProperty("Initials")
        private String initials;

        @JsonProperty("Title")
        private String title;

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getGivenName() {
            return givenName;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public String getInitials() {
            return initials;
        }

        public void setInitials(String initials) {
            this.initials = initials;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFormattedName() {
            StringBuilder sb = new StringBuilder();
            if (title != null && !title.isBlank()) {
                sb.append(title.trim()).append(" ");
            }
            if (givenName != null && !givenName.isBlank()) {
                sb.append(givenName.trim()).append(" ");
            }
            if (familyName != null && !familyName.isBlank()) {
                sb.append(familyName.trim());
            }
            return sb.toString().trim();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GristGrant {
        @JsonProperty("Id")
        private String id;

        @JsonProperty("Doi")
        private String doi;

        @JsonProperty("Title")
        private String title;

        @JsonProperty("Type")
        private String type;

        @JsonProperty("StartDate")
        private String startDate;

        @JsonProperty("EndDate")
        private String endDate;

        @JsonProperty("Funder")
        private GristFunder funder;

        @JsonProperty("Amount")
        private GristAmount amount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public GristFunder getFunder() {
            return funder;
        }

        public void setFunder(GristFunder funder) {
            this.funder = funder;
        }

        public GristAmount getAmount() {
            return amount;
        }

        public void setAmount(GristAmount amount) {
            this.amount = amount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GristFunder {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("FundRefID")
        private String fundRefId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFundRefId() {
            return fundRefId;
        }

        public void setFundRefId(String fundRefId) {
            this.fundRefId = fundRefId;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GristAmount {
        @JsonProperty("value")
        private Double value;

        @JsonProperty("Currency")
        private String currency;

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GristInstitution {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("RORID")
        private String rorId;

        @JsonProperty("RorOfficialName")
        private String rorOfficialName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRorId() {
            return rorId;
        }

        public void setRorId(String rorId) {
            this.rorId = rorId;
        }

        public String getRorOfficialName() {
            return rorOfficialName;
        }

        public void setRorOfficialName(String rorOfficialName) {
            this.rorOfficialName = rorOfficialName;
        }
    }
}
