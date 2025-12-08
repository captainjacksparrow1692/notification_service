package uzumtech.notification;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
public class NotificationApplication {

    // Точка входа в приложение
    public static void main(String[] args) {
        var app = new SpringApplication(NotificationApplication.class);
        var env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    // Логируем полезные ссылки после старта
    private static void logApplicationStartup(Environment env) {
        var protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        var serverPort = env.getProperty("server.port");
        var contextPath = StringUtils.defaultIfBlank(
            env.getProperty("server.servlet.context-path"),
            ""
        );
        var hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }

        var swaggerPath = StringUtils.defaultIfBlank(
            env.getProperty("springdoc.swagger-ui.path"),
            "/swagger-ui.html"
        );

        log.info("""
                        ----------------------------------------------------------
                        \tApplication '{}' is running! Access URLs:
                        \tLocal: \t\t{}://localhost:{}{}
                        \tExternal: \t{}://{}:{}{}
                        \tProfile(s): \t{}
                        \tSwagger: \t{}://localhost:{}{}{}
                        ----------------------------------------------------------
                        """,
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles(),
            protocol,
            serverPort,
            contextPath,
            swaggerPath.startsWith("/") ? swaggerPath : "/" + swaggerPath
        );
    }
}
