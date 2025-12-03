package uzumtech.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        properties = {
                "spring.application.name=notification-service",
                "server.port=8080",
                "springdoc.swagger-ui.path=/swagger"
        }
)
@ExtendWith(OutputCaptureExtension.class)
public class NotificationStartupLogTest {

    @Test
    void shouldPrintStartupLogs(CapturedOutput output) {
        assertThat(output.getAll())
                .contains("Application 'notification-service' is running!")
                .contains("Local:")
                .contains("External:")
                .contains("Swagger:");
    }
}
