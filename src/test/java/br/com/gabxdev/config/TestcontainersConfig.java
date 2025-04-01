package br.com.gabxdev.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@Profile("testIT")
public class TestcontainersConfig {

    private static final MySQLContainer<?> MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.41-debian"))
                .withDatabaseName("desafio-itau")
                .withEnv("TZ", "UTC")
                .withUrlParam("useTimezone", "true")
                .withUrlParam("serverTimezone", "UTC");
        MYSQL_CONTAINER.start();
    }

    @Bean
    @ServiceConnection
    MySQLContainer<?> mysqlContainer() {
        return MYSQL_CONTAINER;
    }
}
