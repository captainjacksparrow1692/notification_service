package uzumtech.notification.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final AppProperties.Provider provider;

    @Bean
    public RestClient eskizRestClient() {
        return RestClient.builder()
            .baseUrl(provider.getUrl())
            .build();
    }
}
