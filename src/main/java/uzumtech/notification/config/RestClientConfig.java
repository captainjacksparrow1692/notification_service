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

    // Билдер RestClient для повторного использования
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    // Клиент Eskiz с базовым URL из настроек
    @Bean
    public RestClient eskizRestClient() {
        return restClientBuilder()
                .baseUrl(provider.getUrl())
                .build();
    }
}
