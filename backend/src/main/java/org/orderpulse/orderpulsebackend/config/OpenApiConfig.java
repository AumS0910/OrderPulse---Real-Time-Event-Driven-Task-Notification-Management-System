package org.orderpulse.orderpulsebackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI 3.0 documentation.
 * This class sets up Swagger UI and OpenAPI documentation for the REST API.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:OrderPulse}")
    private String applicationName;

    /**
     * Creates and configures the OpenAPI documentation bean.
     * @return OpenAPI instance with configured API information and server details
     */
    @Bean
    public OpenAPI orderPulseOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title(applicationName + " API Documentation")
                .description("REST API documentation for the OrderPulse system")
                .version("1.0.0")
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development server")));
    }
}