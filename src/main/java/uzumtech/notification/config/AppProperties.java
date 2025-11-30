package uzumtech.notification.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private FirebaseProperties firebase = new FirebaseProperties();

    @Getter
    @Setter
    public static class FirebaseProperties {
        private Resource location;
        private Boolean enabled;
    }
}
