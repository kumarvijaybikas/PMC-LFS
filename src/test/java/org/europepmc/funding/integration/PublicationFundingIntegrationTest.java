package org.europepmc.funding.integration;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.europepmc.funding.config.EuropePmcProperties;
import org.europepmc.funding.model.dto.FundingStatus;
import org.europepmc.funding.model.dto.PublicationResponseDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class PublicationFundingIntegrationTest {

    private static MockWebServer mockWebServer;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EuropePmcProperties properties;

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();

        String page1Json = Files.readString(Path.of("src/test/resources/fixtures/europepmc-search-page1.json"));
        String page2Json = Files.readString(Path.of("src/test/resources/fixtures/europepmc-search-page2.json"));
        String grant084323 = Files.readString(Path.of("src/test/resources/fixtures/grist-grant-084323.json"));
        String grantEmpty = Files.readString(Path.of("src/test/resources/fixtures/grist-grant-empty.json"));

        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                String path = request.getPath();
                if (path.contains("/search")) {
                    if (path.contains("cursorMark=CURSOR_PAGE_2")) {
                        return new MockResponse().setHeader("Content-Type", "application/json").setBody(page2Json);
                    }
                    return new MockResponse().setHeader("Content-Type", "application/json").setBody(page1Json);
                } else if (path.contains("/GristAPI/rest/get")) {
                    if (path.contains("084323")) {
                        return new MockResponse().setHeader("Content-Type", "application/json").setBody(grant084323);
                    } else {
                        return new MockResponse().setHeader("Content-Type", "application/json").setBody(grantEmpty);
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        mockWebServer.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void searchPublications_endToEndFlow_shouldReturnEnrichedResults() {
        // Point properties to mock server
        properties.setArticlesApiUrl(mockWebServer.url("/search").toString());
        properties.setGrantsApiUrl(mockWebServer.url("/GristAPI/rest/get").toString());

        String url = "http://localhost:" + port + "/v1/publications?query={query}&limit={limit}&pageSize={pageSize}";

        ResponseEntity<PublicationResponseDto> response = restTemplate.getForEntity(
                url,
                PublicationResponseDto.class,
                "Parkinson's disease",
                2,
                1
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PublicationResponseDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getQuery()).isEqualTo("Parkinson's disease");
        assertThat(body.getReturnedCount()).isEqualTo(2);
        assertThat(body.getPublications()).hasSize(2);

        // First publication
        assertThat(body.getPublications().get(0).getId()).isEqualTo("MED:1001");
        assertThat(body.getPublications().get(0).getFunding()).hasSize(2);
        assertThat(body.getPublications().get(0).getFunding().get(0).getStatus()).isEqualTo(FundingStatus.RESOLVED);
        assertThat(body.getPublications().get(0).getFunding().get(0).getGrantDetails().getFunderName()).isEqualTo("Wellcome Trust");
        assertThat(body.getPublications().get(0).getFunding().get(1).getStatus()).isEqualTo(FundingStatus.UNRESOLVED);

        // Second publication
        assertThat(body.getPublications().get(1).getId()).isEqualTo("MED:1002");
        assertThat(body.getPublications().get(1).getFunding()).hasSize(1);
        assertThat(body.getPublications().get(1).getFunding().get(0).getStatus()).isEqualTo(FundingStatus.RESOLVED);

        // Summary assertions
        assertThat(body.getSummary().getTotalPublicationsReportedWithGrants()).isEqualTo(2);
        assertThat(body.getSummary().getTotalGrantsReported()).isEqualTo(3);
        assertThat(body.getSummary().getTotalGrantsResolved()).isEqualTo(2);
        assertThat(body.getSummary().getTotalGrantsUnresolved()).isEqualTo(1);
        assertThat(body.getSummary().getFunderFrequency()).isNotEmpty();
        assertThat(body.getSummary().getFunderFrequency().get(0).getFunderName()).isEqualTo("Wellcome Trust");
        assertThat(body.getSummary().getFunderFrequency().get(0).getPublicationCount()).isEqualTo(2);

        // Traceability of enrichedGrants
        assertThat(body.getEnrichedGrants()).hasSize(2);
        assertThat(body.getEnrichedGrants().get(0).getAssociatedPublicationIds()).contains("MED:1001", "MED:1002");
    }
}
