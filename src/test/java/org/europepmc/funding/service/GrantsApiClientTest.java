package org.europepmc.funding.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.europepmc.funding.config.EuropePmcProperties;
import org.europepmc.funding.config.RestClientConfig;
import org.europepmc.funding.model.external.GristApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GrantsApiClientTest {

    private MockWebServer mockWebServer;
    private GrantsApiClient grantsApiClient;
    private EuropePmcProperties properties;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        properties = new EuropePmcProperties();
        properties.setGrantsApiUrl(mockWebServer.url("/grist").toString());

        RestClient restClient = new RestClientConfig().restClient(properties);
        grantsApiClient = new GrantsApiClient(restClient, properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchGrantById_shouldReturnGrantAndCacheSubsequentCalls() throws Exception {
        String grantJson = Files.readString(Path.of("src/test/resources/fixtures/grist-grant-084323.json"));

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(grantJson));

        Optional<GristApiResponse> res1 = grantsApiClient.fetchGrantById("084323");
        assertThat(res1).isPresent();
        assertThat(res1.get().getHitCountAsInt()).isEqualTo(1);
        assertThat(res1.get().getRecords()).hasSize(1);
        assertThat(res1.get().getRecords().get(0).getGrant().getTitle())
                .isEqualTo("Establishment of a Centre for Clinical Infectious Diseases Research");

        // Second call for the same grantId should hit cache, NOT trigger another HTTP request
        Optional<GristApiResponse> res2 = grantsApiClient.fetchGrantById("084323");
        assertThat(res2).isPresent();
        assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
    }

    @Test
    void fetchGrantById_shouldReturnEmptyOptional_whenInputIsBlank() {
        Optional<GristApiResponse> res = grantsApiClient.fetchGrantById("   ");
        assertThat(res).isEmpty();
        assertThat(mockWebServer.getRequestCount()).isEqualTo(0);
    }
}
