package org.europepmc.funding.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "europepmc")
public class EuropePmcProperties {

    private String apiVersion;
    private String articlesApiUrl = "https://www.ebi.ac.uk/europepmc/webservices/rest/search";
    private String grantsApiUrl = "https://www.ebi.ac.uk/europepmc/GristAPI/rest/get";
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 10000;
    private int defaultPageSize = 25;
    private int maxLimit = 1000;
    private int cacheExpireMinutes = 30;
    private int cacheMaxSize = 5000;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getArticlesApiUrl() {
        return articlesApiUrl;
    }

    public void setArticlesApiUrl(String articlesApiUrl) {
        this.articlesApiUrl = articlesApiUrl;
    }

    public String getGrantsApiUrl() {
        return grantsApiUrl;
    }

    public void setGrantsApiUrl(String grantsApiUrl) {
        this.grantsApiUrl = grantsApiUrl;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(int maxLimit) {
        this.maxLimit = maxLimit;
    }

    public int getCacheExpireMinutes() {
        return cacheExpireMinutes;
    }

    public void setCacheExpireMinutes(int cacheExpireMinutes) {
        this.cacheExpireMinutes = cacheExpireMinutes;
    }

    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }
}
