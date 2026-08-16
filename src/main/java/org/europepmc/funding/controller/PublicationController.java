package org.europepmc.funding.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.europepmc.funding.model.dto.PublicationResponseDto;
import org.europepmc.funding.service.PublicationSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "Publications & Funding", description = "Endpoints for searching scientific publications and enriched funding information")
public class PublicationController {

    private static final Logger log = LoggerFactory.getLogger(PublicationController.class);

    private final PublicationSearchService searchService;

    public PublicationController(PublicationSearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Main endpoint: GET /publications?query=...&limit=25
     */
    @Operation(
            summary = "Search publications and enrich with grant funding details",
            description = "Queries the Europe PMC Articles REST API, resolves funding grants via Europe PMC Grants (Grist) API, aggregates top funders, and returns enriched publication metadata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved and enriched publications",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PublicationResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters or limit out of range",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Upstream Europe PMC API error or bad gateway",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/v1/publications")
    public ResponseEntity<PublicationResponseDto> getPublications(
            @Parameter(description = "Search query adhering to Europe PMC syntax", example = "Parkinson's disease AND mitochondrial dysfunction", required = true)
            @RequestParam(value = "query", required = true) String query,

            @Parameter(description = "Maximum number of publications to return (1 - 1000)", example = "25")
            @RequestParam(value = "limit", required = false) Integer limit,

            @Parameter(description = "Batch page size per upstream request (1 - 100)", example = "25")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,

            @Parameter(description = "Optional cursor mark for deep pagination across pages", example = "*")
            @RequestParam(value = "cursorMark", required = false) String cursorMark) {

        long startTime = System.currentTimeMillis();
        log.info(">> Incoming HTTP GET /v1/publications | query='{}', limit={}, pageSize={}, cursorMark='{}'",
                query, limit, pageSize, cursorMark);

        PublicationResponseDto response = searchService.searchAndEnrichPublications(query, limit, pageSize, cursorMark);

        long duration = System.currentTimeMillis() - startTime;
        log.info("<< Completed HTTP GET /v1/publications in {} ms | returned {}/{} publications, resolved {} grants",
                duration, response.getReturnedCount(), response.getTotalAvailableHits(),
                response.getSummary() != null ? response.getSummary().getTotalGrantsResolved() : 0);

        return ResponseEntity.ok(response);
    }
}
