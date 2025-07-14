package com.dailyquest.dailyquest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration

public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DailyQuest API 문서")
                        .version("1.0.0")
                        .description("DailyQuest 프로젝트의 Swagger API 문서입니다."));
    }
}
