package org.europepmc.funding.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.europepmc.funding.config.EuropePmcProperties;
import org.europepmc.funding.model.external.GristApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

@Service
public class GrantsApiClient {

    private static final Logger log = LoggerFactory.getLogger(GrantsApiClient.class);

    private final RestClient restClient;
    private final EuropePmcProperties properties;
    private final Cache<String, GristApiResponse> grantCache;

    public GrantsApiClient(RestClient restClient, EuropePmcProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
        this.grantCache = Caffeine.newBuilder()
                .maximumSize(properties.getCacheMaxSize())
                .expireAfterWrite(Duration.ofMinutes(properties.getCacheExpireMinutes()))
                .build();
    }

    /**
     * Resolves grant info by grantId from Europe PMC Grist API with caching.
     * In future it must be go with redis or opensearch instead of caffeine.
     * We can use redis or opensearch for caching and storing grant information 
     * for fast retrieval and to avoid multiple API calls.
     */
    public Optional<GristApiResponse> fetchGrantById(String grantId) {
        if (grantId == null || grantId.trim().isEmpty()) {
            return Optional.empty();
        }

        String normalizedGrantId = grantId.trim();

        GristApiResponse cached = grantCache.getIfPresent(normalizedGrantId);
        if (cached != null) {
            log.info("Europe PMC Grist Cache HIT for grantId={}", normalizedGrantId);
            return Optional.of(cached);
        }

        try {
            // Build query: grant_id:"<grantId>" (quoted to safely handle spaces/symbols)
            String queryParam = "grant_id:\"" + normalizedGrantId + "\"";
            URI uri = UriComponentsBuilder.fromUriString(properties.getGrantsApiUrl())
                    .queryParam("query", queryParam)
                    .queryParam("resultType", "core")
                    .queryParam("format", "json")
                    .build()
                    .toUri();

            log.info("--> Calling Europe PMC Grist API for grantId={}: {}", normalizedGrantId, uri);

            GristApiResponse response = restClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, resp) -> {
                        log.debug("Grant ID '{}' not found in Europe PMC Grist database (HTTP 404)", normalizedGrantId);
                    })
                    .body(GristApiResponse.class);

            if (response != null) {
                int hitCount = response.getHitCountAsInt();
                log.info("<-- Received Grist API response for grantId={}: {} hit(s)", normalizedGrantId, hitCount);
                grantCache.put(normalizedGrantId, response);
                return Optional.of(response);
            }
        } catch (RestClientException ex) {
            log.debug("Grant ID '{}' could not be resolved from Grist API: {}", normalizedGrantId, ex.getMessage());
        }

        return Optional.empty();
    }
}
