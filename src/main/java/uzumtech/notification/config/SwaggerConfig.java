package uzumtech.notification.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

@Configuration
public class SwaggerConfig {
    private static final String SECURITY_SCHEME_NAME = "Basic Token";

    @Bean
    public OpenAPI openApiConfiguration(
            @Value("${openapi.title}") final String title,
            @Value("${openapi.version}") final String version,
            @Value("${openapi.description}") final String description,
            @Value("${openapi.server-url:http://localhost:8080}") final String serverUrl
    ) {
        return new OpenAPI()
                .info(new Info()
                        .title(title + " (updated: " + new Date() + ")")
                        .version(version)
                        .description(description)
                        .termsOfService("Terms of service")
                )
                .servers(List.of(new Server().url(serverUrl).description("Backend API")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                    .addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                            .name(SECURITY_SCHEME_NAME)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("basic")
                    )
                );
    }
}