package br.com.gabxdev.config;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static br.com.gabxdev.commons.Constants.BASE_URL;
import static br.com.gabxdev.commons.Constants.TRANSACAO_PATH;

@TestConfiguration
@Lazy
public class RestAssuredConfig {

    @LocalServerPort
    private int port;

    @Bean
    public RequestSpecification requestSpecification() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .basePath(TRANSACAO_PATH)
                .port(port);
    }
}
