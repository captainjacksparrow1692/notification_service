package uzumtech.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

 public class NotificationApplicationStartTest {

    @Test
    void whenApplicationStarts_thenContextIsNotNull() {
        ConfigurableApplicationContext ctx =
                new SpringApplicationBuilder(NotificationApplication.class)
                        .run("--server.port=0");

        assertNotNull(ctx);
        ctx.close();
    }
}
