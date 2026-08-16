package org.europepmc.funding.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.europepmc.funding.config.EuropePmcProperties;
import org.europepmc.funding.config.RestClientConfig;
import org.europepmc.funding.exception.EuropePmcApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EuropePmcClientTest {

    private MockWebServer mockWebServer;
    private EuropePmcClient europePmcClient;
    private EuropePmcProperties properties;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        properties = new EuropePmcProperties();
        properties.setArticlesApiUrl(mockWebServer.url("/search").toString());
        properties.setDefaultPageSize(1);
        properties.setMaxLimit(100);

        RestClient restClient = new RestClientConfig().restClient(properties);
        europePmcClient = new EuropePmcClient(restClient, properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void searchArticles_shouldPaginateAndRetrieveRequestedLimit() throws Exception {
        String page1Json = Files.readString(Path.of("src/test/resources/fixtures/europepmc-search-page1.json"));
        String page2Json = Files.readString(Path.of("src/test/resources/fixtures/europepmc-search-page2.json"));

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(page1Json));
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(page2Json));

        EuropePmcClient.SearchResult result = europePmcClient.searchArticles("Parkinson", 2, 1);

        assertThat(result.totalHits()).isEqualTo(2);
        assertThat(result.articles()).hasSize(2);
        assertThat(result.articles().get(0).getId()).isEqualTo("1001");
        assertThat(result.articles().get(1).getId()).isEqualTo("1002");

        assertThat(mockWebServer.getRequestCount()).isEqualTo(2);
        RecordedRequest req1 = mockWebServer.takeRequest();
        assertThat(req1.getRequestUrl().queryParameter("cursorMark")).isEqualTo("*");
        RecordedRequest req2 = mockWebServer.takeRequest();
        assertThat(req2.getRequestUrl().queryParameter("cursorMark")).isEqualTo("CURSOR_PAGE_2");
    }

    @Test
    void searchArticles_shouldThrowEuropePmcApiException_whenApiReturns500() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        assertThatThrownBy(() -> europePmcClient.searchArticles("Parkinson", 10, 10))
                .isInstanceOf(EuropePmcApiException.class)
                .hasMessageContaining("Europe PMC search request failed");
    }
}
