package com.company.applicationtools.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Application Tools API")
                        .version("1.0")
                        .description("API for Check Employee and National User Options")
                        .contact(new Contact()
                                .name("EON Team")
                                .email("eon-team@company.com")));
    }
}
