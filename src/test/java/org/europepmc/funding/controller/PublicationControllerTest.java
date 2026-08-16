package org.europepmc.funding.controller;

import org.europepmc.funding.exception.InvalidSearchQueryException;
import org.europepmc.funding.model.dto.PublicationResponseDto;
import org.europepmc.funding.model.dto.SearchSummaryDto;
import org.europepmc.funding.service.PublicationSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PublicationController.class)
class PublicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicationSearchService publicationSearchService;

    @Test
    void getPublications_shouldReturn200_whenValidQueryProvided() throws Exception {
        PublicationResponseDto response = new PublicationResponseDto();
        response.setQuery("Parkinson");
        response.setRequestedLimit(25);
        response.setReturnedCount(0);
        response.setTotalAvailableHits(0);
        response.setSummary(new SearchSummaryDto());
        response.setPublications(Collections.emptyList());

        when(publicationSearchService.searchAndEnrichPublications(eq("Parkinson"), eq(25), any(), any()))
                .thenReturn(response);

        mockMvc.perform(get("/v1/publications")
                        .param("query", "Parkinson")
                        .param("limit", "25")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.query").value("Parkinson"))
                .andExpect(jsonPath("$.requestedLimit").value(25));
    }

    @Test
    void getPublications_shouldReturn400_whenQueryParamIsMissing() throws Exception {
        mockMvc.perform(get("/v1/publications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Missing Required Parameter"));
    }

    @Test
    void getPublications_shouldReturn400_whenLimitIsInvalid() throws Exception {
        when(publicationSearchService.searchAndEnrichPublications(eq("Parkinson"), eq(2000), any(), any()))
                .thenThrow(new InvalidSearchQueryException("Requested limit (2000) exceeds maximum allowed (1000)."));

        mockMvc.perform(get("/v1/publications")
                        .param("query", "Parkinson")
                        .param("limit", "2000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Search Parameters"))
                .andExpect(jsonPath("$.detail").value("Requested limit (2000) exceeds maximum allowed (1000)."));
    }
}
