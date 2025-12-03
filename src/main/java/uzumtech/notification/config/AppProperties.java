package uzumtech.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private FirebaseProperties firebase = new FirebaseProperties();
    private Provider provider = new Provider();

    @Getter
    @Setter
    @Configuration
    public static class FirebaseProperties {
        private Resource location;
        private Boolean enabled;
    }

    @Getter
    @Setter
    @Configuration
    public static class Provider {
        private String url;
    }
}