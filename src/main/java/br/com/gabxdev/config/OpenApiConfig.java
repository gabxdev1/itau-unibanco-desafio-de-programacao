package br.com.gabxdev.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Itaú Unibanco - Desafio de Programação",
        version = "V1",
        contact = @Contact(name = "Gabriel Oliveira Durães", url = "https://gabxdev.com.br", email = "bielepicgame14@gmail.com"))
)
public class OpenApiConfig {
}
