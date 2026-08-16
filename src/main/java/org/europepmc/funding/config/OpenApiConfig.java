package org.europepmc.funding.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private final EuropePmcProperties properties;

    public OpenApiConfig(EuropePmcProperties properties) {
        this.properties = properties;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Europe PMC Literature Funding Service API")
                        .version(properties.getApiVersion())
                        .description("REST API service that queries Europe PMC scientific publications, enriches publication metadata with grant records from Europe PMC Grants (Grist) API, and aggregates funder statistics.")
                        .contact(new Contact()
                                .name("Europe PMC Integration Lead")
                                .url("https://europepmc.org"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
